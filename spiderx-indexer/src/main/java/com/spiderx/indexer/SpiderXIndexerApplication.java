package com.spiderx.indexer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.spiderx")
public class SpiderXIndexerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpiderXIndexerApplication.class, args);
    }
}
