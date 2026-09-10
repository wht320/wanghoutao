# Nacos 使用示例

本仓库用一套最小可运行的 Spring Cloud Alibaba 工程，演示 Nacos 最常见的两种用法：

1. **配置中心**：应用启动时从 Nacos 拉取配置，控制台修改后自动刷新。
2. **服务发现**：Provider 注册到 Nacos，Consumer 通过服务名（OpenFeign）调用，而不是写死 IP。

```text
nacos-consumer (8083)  --Feign 服务名-->  nacos-provider (8082)
         \                                      /
          \--------- 注册 / 发现 --------------/
                         |
                   Nacos Server
                   (8848 / 9848)
                         |
              nacos-config-demo (8081)
              拉取 demo.message 并动态刷新
```

## 版本对应

| 组件 | 版本 |
| --- | --- |
| JDK | 17+ |
| Spring Boot | 3.2.12 |
| Spring Cloud | 2023.0.5 |
| Spring Cloud Alibaba | 2023.0.3.4 |
| Nacos Server / Client | 2.4.3 |

Spring Cloud Alibaba 2023.x 对应 Spring Boot 3.2.x。换版本时不要只升 Nacos 客户端，建议整套 BOM 一起对齐。

## 1. 启动 Nacos

控制台地址：<http://127.0.0.1:8848/nacos>

本示例默认关闭鉴权，方便本地体验。生产环境务必开启鉴权，并用 `NACOS_USERNAME` / `NACOS_PASSWORD` 注入账号。

### 方式 A：Docker Compose（推荐）

```bash
docker compose up -d
```

首次启动大约需要几十秒。就绪后可探测：

```bash
curl http://127.0.0.1:8848/nacos/v1/console/health/readiness
```

### 方式 B：官方发行包

1. 从 [Nacos Releases](https://github.com/alibaba/nacos/releases) 下载 `nacos-server-2.4.3`。
2. 解压后进入 `nacos/bin`。
3. 单机启动：

```bash
# Linux / macOS
sh startup.sh -m standalone

# Windows
startup.cmd -m standalone
```

Nacos 2.x 除 `8848` HTTP 端口外，客户端还会走 `9848` gRPC 端口，防火墙需要一起放行。

## 2. 配置中心怎么用

Nacos 里一份配置由三个坐标唯一确定：

| 坐标 | 本示例取值 | 说明 |
| --- | --- | --- |
| `namespace` | `public`（默认） | 环境隔离，如 `dev` / `prod` |
| `group` | `DEFAULT_GROUP` | 业务分组 |
| `dataId` | `nacos-config-demo.yaml` | 配置文件名，默认是 `{spring.application.name}.{file-extension}` |

Spring Boot 3 / Spring Cloud 2023 不再依赖 `bootstrap.yml`，改用 `spring.config.import`：

```yaml
spring:
  application:
    name: nacos-config-demo
  config:
    import:
      - optional:nacos:${spring.application.name}.yaml
  cloud:
    nacos:
      server-addr: 127.0.0.1:8848
      config:
        file-extension: yaml
        group: DEFAULT_GROUP
        refresh-enabled: true
```

`optional:` 表示 Nacos 暂时连不上时应用仍能启动，本地调试更方便。生产建议去掉 `optional:`，避免带着空配置上线。

发布一份初始配置：

```bash
chmod +x scripts/*.sh
./scripts/init-nacos-config.sh
```

等价于在控制台「配置管理 → 配置列表 → 创建配置」写入：

```yaml
demo:
  message: hello from nacos
  version: v1
```

应用侧用 `@ConfigurationProperties` 绑定。Nacos 推送变更后，Spring Cloud 会重绑定属性，无需重启：

```java
@ConfigurationProperties(prefix = "demo")
public class DemoProperties {
    private String message;
    private String version;
}
```

验证：

```bash
# 编译并启动配置示例
mvn -pl nacos-config-demo -am spring-boot:run

curl http://127.0.0.1:8081/config
# {"message":"hello from nacos","version":"v1"}
```

然后到控制台把 `demo.message` 改成别的值，再请求一次 `/config`，应看到新内容。

## 3. 服务发现怎么用

### Provider：只负责注册自己

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
```

```yaml
spring:
  application:
    name: nacos-provider
  cloud:
    nacos:
      server-addr: 127.0.0.1:8848
      discovery:
        group: DEFAULT_GROUP
```

`spring.application.name` 就是注册到 Nacos 的服务名。

### Consumer：用服务名调用，不要写 IP

```java
@FeignClient(name = "nacos-provider")
public interface ProviderClient {
    @GetMapping("/hello")
    Map<String, Object> hello(@RequestParam("name") String name);
}
```

启动顺序：Nacos → `nacos-provider` → `nacos-consumer`。

```bash
mvn -pl nacos-provider -am spring-boot:run
mvn -pl nacos-consumer -am spring-boot:run

curl http://127.0.0.1:8082/hello?name=nacos
curl http://127.0.0.1:8083/hello?name=nacos
```

打开控制台「服务管理 → 服务列表」，应能看到 `nacos-provider`、`nacos-consumer`（以及配置示例 `nacos-config-demo`）。

也可以一次跑完全部探测：

```bash
./scripts/verify.sh
```

## 4. 工程结构

```text
.
├── docker-compose.yml          # 单机 Nacos
├── pom.xml                     # 父工程，统一 BOM
├── scripts/
│   ├── init-nacos-config.sh    # 向 Nacos 发布示例配置
│   └── verify.sh               # 三个服务的 HTTP 探测
├── nacos-config-demo/          # 配置中心，端口 8081
├── nacos-provider/             # 服务提供者，端口 8082
└── nacos-consumer/             # 服务消费者，端口 8083
```

常用环境变量：

| 变量 | 默认值 | 含义 |
| --- | --- | --- |
| `NACOS_SERVER_ADDR` | `127.0.0.1:8848` | Nacos 地址 |
| `NACOS_USERNAME` | 空 | 开启鉴权时填写 |
| `NACOS_PASSWORD` | 空 | 开启鉴权时填写 |

## 5. 常见问题

**启动报错 `No spring.config.import set`**  
Spring Cloud Alibaba 2023 必须写 `spring.config.import: nacos:...`，只配 `spring.cloud.nacos.config.server-addr` 不够。

**控制台能打开，客户端连不上**  
检查 `9848` 是否对客户端开放。Docker / 云主机只映射 `8848` 时，gRPC 注册会失败。

**改了配置接口还是旧值**  
确认 DataId 为 `nacos-config-demo.yaml`、Group 为 `DEFAULT_GROUP`、格式为 YAML。`@Value` 需要 `@RefreshScope`；本示例用的 `@ConfigurationProperties` 会随 Environment 自动重绑定。

**Consumer 报 `No instances available for nacos-provider`**  
Provider 还没注册成功，或两边 `namespace` / `group` 不一致。先看控制台服务列表是否有健康实例。

**本地没有 Docker**  
用官方 `startup.sh -m standalone` 启动即可，应用配置不用改。

## 6. 生产使用建议

- 按环境拆 namespace，不要把 dev / prod 配在同一个 `public` 里。
- 打开 Nacos 鉴权，配置走密钥或环境变量，不要写进仓库。
- 核心配置不要用 `optional:nacos`，避免降级成默认值。
- 服务下线配合优雅停机，减少调用方打到已关闭实例。
- 配置变更要可回滚：控制台自带历史版本，发布前先确认 DataId。
