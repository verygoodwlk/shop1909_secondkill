package com.qf.listener;

import com.qf.service.IGoodsService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class MyRabbitMqListener {

    @Autowired
    private IGoodsService goodsService;

    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(type = "fanout", name = "kill_exchange"),
                    value = @Queue(name = "kill_goods_queue", durable = "true")
            )
    )
    public void killMsgHandler(Map<String, Object> map, Channel channel, Message message){
        System.out.println("接收到秒杀信息：" + map);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //商品id
        Integer gid = (Integer) map.get("gid");
        Integer gnumber = (Integer) map.get("gnumber");

        //同步修改库存
        goodsService.updateKillSave(gid, gnumber);

        //手动确认消息
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
