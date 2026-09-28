# SPRCP：内网 11.2 → U 盘 → Mac DBeaver

按你确认的范围：

| 项 | 取值 |
| --- | --- |
| 源库 | Oracle **11.2** |
| 模式 | **SPRCP** 下全部表 |
| 数据量 | **每表最多 500 行** |
| 外网工具 | Mac **DBeaver 23.3.4** |
| 目标库 | Docker Oracle Free，`localhost:1521/FREEPDB1` |

11.2 不能用 `FETCH FIRST`，截取必须用 `ROWNUM <= 500`。不要用 Data Pump（`.dmp`）：11.2 的 dump 很难进 23ai Free。

先把本目录整份拷进 U 盘带到内网；导出完成后再把 `export/` 拷回 Mac。

建议的 U 盘目录：

```text
usb/sprcp-dev/
  01-list-sprcp-tables.sql
  02-rowcounts.sql
  03-generate-export-sql.sql
  04-mac-create-user.sql
  05-mac-disable-fk.sql
  06-mac-enable-fk.sql
  export/
    schema-sprcp.sql      # 结构
    data/                 # 每表一个 INSERT 或 CSV
    MANIFEST.txt          # 导出日期、表数量、过滤条件
```

## A. 内网 DBeaver 23.3.4 导出

连上内网库，确认当前能看到用户/模式 `SPRCP`。

### A1. 列出全部表

打开 `01-list-sprcp-tables.sql` 执行。把结果表名粘进 `MANIFEST.txt`，记下一共多少张表。

### A2. 导出结构（只要对象定义，不要表空间）

1. 左侧导航：Oracle 连接 → 模式 **SPRCP** → 右键。
2. **生成 SQL → DDL**（英文界面是 Generate SQL → DDL）。
3. 勾选：表、主键、唯一约束、索引、序列。  
   开发用不到可先不勾：物化视图、DB Link、JOB、JAVA 源。
4. 打开 DDL 生成选项，尽量去掉：
   - Tablespace / 表空间
   - Storage / 存储子句
   - Owner 前缀（或生成后全局改成 `SPRCP.` 与本地用户一致）
5. 保存为 `export/schema-sprcp.sql`。

导入 23ai 时若报 `ORA-00959 tablespace ... does not exist`，用编辑器删掉所有 `TABLESPACE xxx` 和 `STORAGE (...)` 再执行。

### A3. 导出数据（每表 ≤ 500 行）

表不多、且单表体积不大时：

1. 导航打开 **SPRCP → 表**，全选全部表。
2. 右键 **导出数据**（Export Data）。
3. 格式选 **SQL** → **INSERT**。
4. Extraction / 提取设置里把 **Max rows / 最大行数** 设为 **500**。
5. 输出选 **每个表一个文件**，目录 `export/data/`。
6. 编码 **UTF-8**。

单表上百万行时，DBeaver 的 Max rows 仍可能先扫表，会很慢。改用：

1. 执行 `03-generate-export-sql.sql`。
2. 对每张大表：SQL 编辑器跑 `SELECT * FROM SPRCP.表名 WHERE ROWNUM <= 500`。
3. 结果网格 → 右键 **导出数据** → SQL INSERT 或 CSV，保存到 `export/data/表名.sql`。

11.2 不要写成 `FETCH FIRST 500 ROWS ONLY`。

### A4. 带走

把 `export/` 和 `MANIFEST.txt` 拷到 U 盘。不要带内网连接配置、生产密码。

## B. Mac：空库 + DBeaver 连接

1. 打开 Docker Desktop。
2. 仓库里：

```bash
cd oracle
cp .env.example .env
docker compose up -d
docker compose logs -f
```

等到 `DATABASE IS READY TO USE!`。

3. DBeaver 23.3.4 新建 Oracle 连接：

| 项 | 值 |
| --- | --- |
| 主机 | `localhost` |
| 端口 | `1521` |
| 连接类型 | 服务名 |
| 服务名 | `FREEPDB1` |
| 用户 | `system` |
| 密码 | `Oracle123`（`oracle/.env` 里的 `ORACLE_PWD`） |

4. 用 **system** 打开并执行 `04-mac-create-user.sql`（本地用户 `sprcp` / `SprcpDev123`）。
5. 再新建一条 DBeaver 连接，用户改成 `sprcp`，密码 `SprcpDev123`，服务名仍是 `FREEPDB1`。后面导入都走这条。

## C. Mac DBeaver 导入

用 **sprcp** 连接。

1. 执行 `export/schema-sprcp.sql`。失败就按报错删表空间/存储子句，或跳过依赖其他业务用户的对象。
2. 执行 `05-mac-disable-fk.sql`。每表只导 500 行，子表外键往往对不上父表，必须先禁用。
3. **SQL 编辑器 → 执行脚本**：按批跑 `export/data/*.sql`。  
   CSV 则：表上右键 **导入数据**，编码 UTF-8，日期格式按源库（常见 `yyyy-MM-dd HH:mm:ss`）。
4. 抽查：

```sql
SELECT table_name, num_rows FROM user_tables ORDER BY table_name;
SELECT COUNT(*) FROM 某张关键表;
```

`num_rows` 要统计信息更新后才准，抽查用 `COUNT(*)`。

5. 可选：执行 `06-mac-enable-fk.sql`。报父键不存在就保持外键禁用，不影响本机查数、写代码。

## D. 以后增量

过滤条件固定为 `ROWNUM <= 500`（需要「最近数据」时改成带业务时间列的 `WHERE`，再套一层 `ROWNUM`）。重复 A3 → U 盘 → C3 覆盖 `export/data/` 即可。结构变了再重导 DDL。

## 注意

- 这是开发切片，不是备份，不能当生产恢复。
- 500 行截取和外键、序列当前值可能不一致；本地序列如对不上，对单表 `SELECT MAX(id)` 后 `ALTER SEQUENCE ... RESTART`（23ai）或 `DROP/CREATE SEQUENCE`。
- `LONG`、`BFILE`、DB Link 在 DBeaver INSERT 里容易失败，本机开发可先空着这些列。
- 真实客户数据能脱敏就脱敏；不要把导出文件提交进 Git。
