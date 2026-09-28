package com.wanghoutao.nacos.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class NacosConfigDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(NacosConfigDemoApplication.class, args);
    }
}
