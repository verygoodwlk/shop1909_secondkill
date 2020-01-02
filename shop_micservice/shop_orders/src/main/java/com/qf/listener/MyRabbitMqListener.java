package com.qf.listener;

import com.qf.serviceimpl.IOrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class MyRabbitMqListener {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(type = "fanout", name = "kill_exchange"),
                    value = @Queue(name = "kill_orders_queue", durable = "true")
            )
    )
    public void killMsgHandler(Map<String, Object> map, Channel channel, Message message){
        System.out.println("订单服务接收到秒杀信息：" + map);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //
        Integer gid = (Integer) map.get("gid");
        Integer uid = (Integer) map.get("uid");
        Integer gnumber = (Integer) map.get("gnumber");

        //保存订单
        orderService.insertOrders(gid, uid, gnumber);

        //移除排队
        redisTemplate.opsForZSet().remove("paidui_" + gid, uid + "");

        //手动确认消息
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
