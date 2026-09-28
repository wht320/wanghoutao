package com.wanghoutao.nacos.consumer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "nacos-provider")
public interface ProviderClient {

    @GetMapping("/hello")
    Map<String, Object> hello(@RequestParam("name") String name);
}
