# 本机 Oracle 数据库（Docker）

Mac 没有官方原生安装包，本地开发用 Oracle Database Free 容器。Apple Silicon（M1/M2/M3/M4）用官方 ARM 镜像，不必再套虚拟机。

## 启动

先打开 Docker Desktop，确认守护进程已运行。

```bash
cd oracle
cp .env.example .env   # 按需改密码
docker compose up -d
docker compose logs -f
```

第一次拉镜像较慢。日志出现 `DATABASE IS READY TO USE!` 后再连。

若提示无权拉取镜像：

```bash
docker login container-registry.oracle.com
```

Intel Mac 若自动拉到不兼容架构，可在 `docker-compose.yml` 里给服务加上：

```yaml
platform: linux/amd64
```

## 连接

| 项 | 值 |
| --- | --- |
| 主机 | `localhost` |
| 端口 | `1521` |
| 服务名（PDB） | `FREEPDB1` |
| CDB | `FREE` |
| 用户 | `system` / `sys` / `pdbadmin` |
| 密码 | `.env` 里的 `ORACLE_PWD`，默认 `Oracle123` |
| JDBC | `jdbc:oracle:thin:@localhost:1521/FREEPDB1` |

容器内执行 SQL：

```bash
docker exec -it oracle-free sqlplus system/Oracle123@FREEPDB1
```

停 / 再开 / 删除容器（数据在 volume 里会保留）：

```bash
docker compose stop
docker compose start
docker compose down
```

清掉数据：

```bash
docker compose down -v
```

## 限制

这是 **Free** 开发版：大约 2 CPU、2GB 内存、用户数据约 12GB，只适合本机学习。生产请用授权的 Oracle 安装，不要把默认密码用于真实环境。
