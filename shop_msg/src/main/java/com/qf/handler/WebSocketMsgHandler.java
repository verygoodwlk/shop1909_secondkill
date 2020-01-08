package com.qf.handler;

import com.alibaba.fastjson.JSON;
import com.qf.entity.WsMsgEntity;
import com.qf.util.ChannelUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class WebSocketMsgHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("有一个客户端连接！");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("有一个客户端断开连接！");

        //下线后移除关系
        Channel channel = ctx.channel();
        ChannelUtil.removeChannel(ChannelUtil.getUid(channel));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) throws Exception {
//        System.out.println("接收到websocket的数据：" + textWebSocketFrame.text());

        //消息字符串
        String msg = textWebSocketFrame.text();
        //将消息字符串转换成实体类
        WsMsgEntity wsMsgEntity = JSON.parseObject(msg, WsMsgEntity.class);
        //往后传递
        ctx.fireChannelRead(wsMsgEntity);
    }
}
