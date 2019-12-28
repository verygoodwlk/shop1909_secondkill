package com.qf.shop_resources;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ShopResourcesApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopResourcesApplication.class, args);
    }

}
