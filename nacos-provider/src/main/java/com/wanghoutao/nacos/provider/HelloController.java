package com.wanghoutao.nacos.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HelloController {

    @Value("${server.port}")
    private int port;

    @Value("${spring.application.name}")
    private String serviceName;

    @GetMapping("/hello")
    public Map<String, Object> hello(@RequestParam(defaultValue = "nacos") String name) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("service", serviceName);
        body.put("port", port);
        body.put("message", "hello, " + name);
        return body;
    }
}
