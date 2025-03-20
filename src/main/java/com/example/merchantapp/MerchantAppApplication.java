package com.example.merchantapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 启动类 (含 main 方法)
 * 启动应用时，加载 Spring Boot 配置
 */
@SpringBootApplication
public class MerchantAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(MerchantAppApplication.class, args);
    }
}
