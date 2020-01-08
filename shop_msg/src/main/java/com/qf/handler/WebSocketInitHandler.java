package com.qf.handler;

import com.qf.entity.WsMsgEntity;
import com.qf.util.ChannelUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class WebSocketInitHandler extends SimpleChannelInboundHandler<WsMsgEntity> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WsMsgEntity wsMsgEntity) throws Exception {
        if(wsMsgEntity.getType() == 1){

            //线程1  18 - Channel1
            //线程2  29 - Channel2

            //初始化消息
            Integer uid = (Integer) wsMsgEntity.getData();
            
            //获得客户端对应的Channel
            Channel channel = ctx.channel();

            //管理当前用户id - channel的映射关系
            ChannelUtil.add(uid, channel);

        } else {
            ctx.fireChannelRead(wsMsgEntity);
        }
    }
}
