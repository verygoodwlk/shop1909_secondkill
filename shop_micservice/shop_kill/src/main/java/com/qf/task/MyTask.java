package com.qf.task;

import com.qf.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyTask {

    @Autowired
    private StringRedisTemplate redisTemplate;

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
}
