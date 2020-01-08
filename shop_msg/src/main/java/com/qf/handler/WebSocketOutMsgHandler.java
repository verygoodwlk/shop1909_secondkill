package com.qf.handler;

import com.alibaba.fastjson.JSON;
import com.qf.entity.WsMsgEntity;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Component;

/**
 * 出站消息的处理器
 */
@Component
@ChannelHandler.Sharable
public class WebSocketOutMsgHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        if(msg instanceof WsMsgEntity){
            String str = JSON.toJSONString(msg);
            super.write(ctx, new TextWebSocketFrame(str), promise);
        } else {
            super.write(ctx, msg, promise);
        }
    }
}
