package com.qf.shop_orders;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.qf")
@EnableEurekaClient
@MapperScan("com.qf.dao")
@EnableFeignClients(basePackages = "com.qf.feign")
public class ShopOrdersApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopOrdersApplication.class, args);
    }
}
