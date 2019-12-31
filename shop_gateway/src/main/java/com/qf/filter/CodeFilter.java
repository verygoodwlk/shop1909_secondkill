package com.qf.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 验证码判定的过滤器
 *
 * GatewayFilter - 路由网关过滤器需要实现的接口， 重写filter方法
 * Ordered - 路由网关过滤器顺序的实现接口
 */
@Component
public class CodeFilter implements GatewayFilter, Ordered {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * WebFlux - 函数式编程
     * 过滤逻辑实现
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //获得cookie中的codeToken
        ServerHttpRequest request = exchange.getRequest();
        HttpCookie codeToken = request.getCookies().getFirst("codeToken");

        //获得参数code
        String code = request.getQueryParams().getFirst("code");

        if(codeToken != null){
            String token = codeToken.getValue();//获得验证码的cookie

            //获得服务端存储的验证码
            String redisCode = redisTemplate.opsForValue().get(token);

            if(redisCode != null && redisCode.equalsIgnoreCase(code)){
                //验证通过，放行请求
                return chain.filter(exchange);
            }
        }


        //验证码未通过
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.SEE_OTHER);//设置响应码
        //设置重定向的位置
        String msg = null;
        try {
            msg = URLEncoder.encode("验证码错误！", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.getHeaders().set("Location", "/info/error?msg=" + msg);

        return response.setComplete();
    }

    /**
     * 返回当前过滤器的优先级 值越小，优先级越大
     * @return
     */
    @Override
    public int getOrder() {
        return 100;
    }
}
