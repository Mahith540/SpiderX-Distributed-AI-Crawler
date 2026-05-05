package com.spiderx.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.spiderx")
public class SpiderXApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpiderXApiApplication.class, args);
    }
}
