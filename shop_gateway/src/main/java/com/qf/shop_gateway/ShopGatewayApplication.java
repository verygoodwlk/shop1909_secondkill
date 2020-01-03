package com.qf.shop_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@SpringBootApplication(scanBasePackages = "com.qf")
@EnableEurekaClient
public class ShopGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopGatewayApplication.class, args);
    }


    @Bean(name = "remoteAddrKeyResolver")
    public KeyResolver getKeyResolver(){
        return new KeyResolver() {
            @Override
            public Mono<String> resolve(ServerWebExchange exchange) {
//                //ip限流
//                return Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
                //url限流
                return Mono.just(exchange.getRequest().getURI().getPath());
            }
        };
    }
}
