package com.wanghoutao.nacos.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "demo")
public class DemoProperties {

    /**
     * 欢迎文案，可在 Nacos 控制台修改后动态生效。
     */
    private String message = "尚未从 Nacos 加载配置";

    /**
     * 配置版本号，便于观察刷新是否成功。
     */
    private String version = "local";

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
