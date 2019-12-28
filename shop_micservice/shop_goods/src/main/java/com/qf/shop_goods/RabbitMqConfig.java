package com.qf.shop_goods;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public Queue getQueue(){
        return new Queue("kill_queue");
    }

    @Bean
    public FanoutExchange getExchange(){
        return new FanoutExchange("goods_exchange");
    }

    @Bean
    public Binding getBinding(Queue getQueue, FanoutExchange getExchange){
        return BindingBuilder.bind(getQueue).to(getExchange);
    }
}
