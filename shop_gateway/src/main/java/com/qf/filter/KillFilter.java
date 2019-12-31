package com.qf.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Component
public class KillFilter implements GatewayFilter, Ordered {

    private String lua = "local gid = ARGV[1]\n" +
            "local times = redis.call('get', 'killgoods_now')\n" +
            "local flag = 0\n" +
            "if times then \n" +
            "flag = redis.call('sismember', 'killgoods_'..times, gid)\n" +
            "end\n" +
            "return flag";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String gid = exchange.getRequest().getQueryParams().getFirst("gid");

        //lua脚本
        Long result = redisTemplate.execute(new DefaultRedisScript<>(lua, Long.class), null, gid);

        //判断当前秒杀是否开始
//        long start = System.currentTimeMillis();

//        String times = redisTemplate.opsForValue().get("killgoods_now");
//        boolean flag = false;
//        if(times != null){
//            flag = redisTemplate.opsForSet().isMember("killgoods_" + times, gid + "");
//        }

//        long end = System.currentTimeMillis();
//        System.out.println("判断是否提前秒杀的耗时：" + (end - start));

        if(result == 1){
            //商品已经可以抢购，放行
            return chain.filter(exchange);
        }

        //抢购商品验证未通过
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.SEE_OTHER);//设置响应码
        //设置重定向的位置
        String msg = null;
        try {
            msg = URLEncoder.encode("商品未开始抢购！", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.getHeaders().set("Location", "/info/error?msg=" + msg);

        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return 200;
    }
}
