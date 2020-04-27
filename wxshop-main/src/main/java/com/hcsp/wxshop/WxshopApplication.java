package com.hcsp.wxshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class WxshopApplication {
    public static void main(String[] args) {
        SpringApplication.run(WxshopApplication.class, args);
    }
}
