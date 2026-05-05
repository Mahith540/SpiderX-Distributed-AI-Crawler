package com.spiderx.crawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.spiderx")
public class SpiderXCrawlerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpiderXCrawlerApplication.class, args);
    }
}
