package com.qf.handler;

import com.qf.entity.WsMsgEntity;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Component;

/**
 * 心跳消息处理器
 */
@Component
@ChannelHandler.Sharable
public class WebSocketHeartHandler extends SimpleChannelInboundHandler<WsMsgEntity> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WsMsgEntity wsMsgEntity) throws Exception {
        if(wsMsgEntity.getType() == 2){
            //心跳消息
            //返回一个消息
            ctx.writeAndFlush(wsMsgEntity);

        } else {
            //如果不是心跳消息，就继续往后传递
            ctx.fireChannelRead(wsMsgEntity);
        }
    }
}
