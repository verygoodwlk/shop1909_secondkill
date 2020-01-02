package com.qf.shop_goods;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class ShopGoodsApplicationTests {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        Long result = redisTemplate.opsForZSet().rank("paidui_53", 192312 + "");
        System.out.println("排名：" + result);
    }

}
