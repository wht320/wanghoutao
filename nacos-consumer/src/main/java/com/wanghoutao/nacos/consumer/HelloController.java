package com.wanghoutao.nacos.consumer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HelloController {

    private final ProviderClient providerClient;

    public HelloController(ProviderClient providerClient) {
        this.providerClient = providerClient;
    }

    @GetMapping("/hello")
    public Map<String, Object> hello(@RequestParam(name = "name", defaultValue = "nacos") String name) {
        return providerClient.hello(name);
    }
}
