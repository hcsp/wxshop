package com.hcsp.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.hcsp.api.generate"})
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

//    @Bean
//    public OrderMapper orderMapper(SqlSessionFactory sqlSessionFactory) {
//        return sqlSessionFactory.openSession(true).getMapper(OrderMapper.class);
//    }
//
//    @Bean
//    public OrderGoodsMapper orderGoodsMapper(SqlSessionFactory sqlSessionFactory) {
//        return sqlSessionFactory.openSession(true).getMapper(OrderGoodsMapper.class);
//    }
}
