# 本机安装 Oracle 数据库，并用你的系统连接

Mac 没有官方数据库安装包。做法是：Docker 在本机起 Oracle，你的系统（SQL 工具或 Java）连 `localhost:1521`。

云端 Agent **不能**替你打开 Mac 上的 Docker，也不能让你的电脑连到云主机里的库。必须在你自己的 Mac 上执行下面步骤。

## 1. 先起数据库

1. 安装并打开 [Docker Desktop](https://www.docker.com/products/docker-desktop/)，菜单栏图标变成 Running。
2. 终端进入本仓库的 `oracle` 目录：

```bash
cd oracle
cp .env.example .env
docker compose up -d
docker compose logs -f
```

3. 等到日志出现 `DATABASE IS READY TO USE!`（首次可能要几分钟）。
4. 另开一个终端确认容器在跑：

```bash
docker ps
# 应能看到 oracle-free ，端口 0.0.0.0:1521->1521
```

拉镜像报未授权时先登录：

```bash
docker login container-registry.oracle.com
```

Intel Mac 若架构不对，在 `docker-compose.yml` 的 `oracle` 服务下加一行 `platform: linux/amd64`。

## 2. 用你的系统连接

连接参数（和 `.env` 里密码保持一致）：

| 项 | 值 |
| --- | --- |
| 主机 | `127.0.0.1` 或 `localhost` |
| 端口 | `1521` |
| 连接类型 | **服务名**（不要选 SID） |
| 服务名 | `FREEPDB1` |
| 用户 | `system` |
| 密码 | `Oracle123` |
| JDBC | `jdbc:oracle:thin:@localhost:1521/FREEPDB1` |

### 方式 A：DBeaver / SQL Developer（图形界面）

1. 新建 Oracle 连接。
2. 主机 `localhost`，端口 `1521`。
3. 服务名填 `FREEPDB1`（不是 `FREE`，`FREE` 是 CDB）。
4. 用户 `system`，密码 `Oracle123`。
5. 测试连接。通过后即可执行：

```sql
SELECT USER, SYSDATE FROM DUAL;
```

### 方式 B：终端（不用再装客户端）

```bash
docker exec -it oracle-free sqlplus system/Oracle123@FREEPDB1
```

### 方式 C：本机 Java JDBC（仓库已带示例）

数据库起来后，在仓库根目录：

```bash
mvn -f oracle-client-demo/pom.xml -q exec:java
```

改密码时：

```bash
ORACLE_PASSWORD=你的密码 mvn -f oracle-client-demo/pom.xml -q exec:java
```

成功时会打印：`连接成功：用户=SYSTEM 数据库时间=...`

## 3. 常见问题

**`Cannot connect to the Docker daemon`**  
Docker Desktop 没启动。

**客户端报 `IO Error` / `The Network Adapter could not establish the connection`**  
容器还没就绪，或本机 1521 被占用。看 `docker compose logs` 是否已 READY；可用 `lsof -i :1521` 查端口。

**ORA-12514 / 找不到服务名**  
服务名必须是 `FREEPDB1`，不要填 `orcl` 或 SID `FREE`。

**ORA-01017 用户名或密码错误**  
密码要和 `oracle/.env` 的 `ORACLE_PWD` 一致。改过 `.env` 后要 `docker compose up -d --force-recreate`（已有数据的容器不会自动改库内密码）。

## 4. 停库

```bash
docker compose stop          # 停，数据还在
docker compose start         # 再开
docker compose down          # 删容器，volume 里的数据仍在
docker compose down -v       # 连数据一起删
```

这是 Free 开发版，大约 2GB 内存，只适合本机学习。
