package com.qf.task;

import com.alibaba.fastjson.JSON;
import com.qf.entity.Goods;
import com.qf.entity.WsMsgEntity;
import com.qf.feign.GoodsFeign;
import com.qf.util.DateUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class MyTask {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private GoodsFeign goodsFeign;

    /**
     * cron表达式：秒 分 时 日 月 [星期] 年
     */
    @Scheduled(cron = "0 0 0/1 * * *")
    public void mytask(){

        //------------------------更新当前的秒杀场次--------------------------------
        //获取原来的场次,并且删除
        String oldtime = redisTemplate.opsForValue().get("killgoods_now");
        redisTemplate.delete("killgoods_" + oldtime);

        //更新redis中的场次时间
        String time = DateUtil.date2String(new Date(), "yyMMddHH");
        redisTemplate.opsForValue().set("killgoods_now", time);

    }

    /**
     * 2:50 3:50
     *
     * 整点前10分钟 -> 每到50分钟的时候提醒
     */
    @Scheduled(cron = "0 50 * * * *")
    public void mytask2(){
        //开始进行秒杀提醒

        //获取当前场次秒杀信息
        Date now = new Date();

        //根据当前时间获得yyMMdd
        String yyMMdd = DateUtil.date2String(now, "yyMMdd");

        //根据当前时间获得HHmm
        String hhmm = DateUtil.date2String(now, "HHmm");

        //查询redis找到对应的秒杀提醒信息
        while(true) {
            Set<String> contents = redisTemplate.opsForZSet().rangeByScore("tixing_" + yyMMdd, Double.valueOf(hhmm), Double.valueOf(hhmm), 0, 100);
//            redisTemplate.opsForZSet().removeRangeByScore("tixing_" + yyMMdd, Double.valueOf(hhmm), Double.valueOf(hhmm));
            redisTemplate.opsForZSet().remove("tixing_" + yyMMdd, contents.toArray());

            //结束循环
            if(contents == null || contents.size() == 0){
                break;
            }

            //循环推送消息
            for (String content : contents) {
                Map map = JSON.parseObject(content, HashMap.class);
                Integer gid = (Integer) map.get("gid");
                Goods goods = goodsFeign.queryById(gid);
                map.put("goods", goods);

                //将消息封装成固定的消息对象
                WsMsgEntity wsMsgEntity = new WsMsgEntity()
                        .setFromid(-1)
                        .setToid((Integer)map.get("uid"))
                        .setType(3)
                        .setData(map);

                //将map对象推送给对应的客户端
                rabbitTemplate.convertAndSend("netty_exchange", "", wsMsgEntity);
            }
        }
    }
}
