package com.qf.shop_msg;

import com.qf.handler.WebSocketHeartHandler;
import com.qf.handler.WebSocketInitHandler;
import com.qf.handler.WebSocketMsgHandler;
import com.qf.handler.WebSocketOutMsgHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ServerStartListener implements CommandLineRunner {

    private EventLoopGroup master = new NioEventLoopGroup();
    private EventLoopGroup slave = new NioEventLoopGroup();

    @Value("${server.port}")
    private int port;

    @Autowired
    private WebSocketMsgHandler webSocketMsgHandler;

    @Autowired
    private WebSocketHeartHandler webSocketHeartHandler;

    @Autowired
    private WebSocketOutMsgHandler outMsgHandler;

    @Autowired
    private WebSocketInitHandler initHandler;

    @Override
    public void run(String... args) throws Exception {

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap
                .group(master,slave)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();

                        pipeline.addLast(new HttpServerCodec());
                        pipeline.addLast(new HttpObjectAggregator(1024 * 1024));
                        pipeline.addLast(new WebSocketServerProtocolHandler("/msg"));
                        //消息超时的处理器
                        pipeline.addLast(new ReadTimeoutHandler(5, TimeUnit.MINUTES));

                        //出站处理器
                        pipeline.addLast(outMsgHandler);

                        //入站处理
                        pipeline.addLast(webSocketMsgHandler);
                        pipeline.addLast(initHandler);
                        pipeline.addLast(webSocketHeartHandler);


                    }
                });

        bootstrap.bind(port).sync();
        System.out.println("消息中心已经启动，端口为：" + port);

    }
}
