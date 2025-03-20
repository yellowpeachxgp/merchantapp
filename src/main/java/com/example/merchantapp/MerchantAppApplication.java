package com.example.merchantapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy  // 启用AOP切面支持
public class MerchantAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(MerchantAppApplication.class, args);
    }
}
