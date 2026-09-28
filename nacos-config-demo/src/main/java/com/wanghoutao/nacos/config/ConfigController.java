package com.wanghoutao.nacos.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class ConfigController {

    private final DemoProperties demoProperties;

    public ConfigController(DemoProperties demoProperties) {
        this.demoProperties = demoProperties;
    }

    @GetMapping("/config")
    public Map<String, Object> config() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", demoProperties.getMessage());
        body.put("version", demoProperties.getVersion());
        return body;
    }
}
