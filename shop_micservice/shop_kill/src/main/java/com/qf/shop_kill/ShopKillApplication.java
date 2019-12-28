package com.qf.shop_kill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.qf")
@EnableEurekaClient
@EnableFeignClients(basePackages = "com.qf.feign")
public class ShopKillApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopKillApplication.class, args);
    }

}
