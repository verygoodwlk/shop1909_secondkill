package com.qf.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

/**
 * 过滤器工厂类
 */
@Component
public class CodeFilterRegister extends AbstractGatewayFilterFactory {

    @Autowired
    private CodeFilter codeFilter;

    @Override
    public GatewayFilter apply(Object config) {
        return codeFilter;
    }

    @Override
    public String name() {
        return "myCode";
    }
}
