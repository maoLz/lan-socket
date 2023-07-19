package com.hyx.lansocket;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
@Slf4j
public class NettyServer {


    private static final String WEBSOCKET_PROTOCOL = "WebSocket";


    @Value("${websocket.netty.port}")
    private int port;

    @Value("${websocket.netty.weboScketPath}")
    private String webSocketPath;

    private EventLoopGroup mainGroup;

    private EventLoopGroup workGroup;

    @Autowired
    WebSocketHandler webSocketHandler;




    private void start() throws InterruptedException {
        mainGroup = new NioEventLoopGroup();
        workGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(mainGroup,workGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.localAddress(new InetSocketAddress(port));
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new HttpServerCodec());
                socketChannel.pipeline().addLast(new ChunkedWriteHandler());
                socketChannel.pipeline().addLast(new HttpObjectAggregator(8192));
                socketChannel.pipeline().addLast(
                        new WebSocketServerProtocolHandler(webSocketPath,WEBSOCKET_PROTOCOL,true,65536*10)
                );
                socketChannel.pipeline().addLast(webSocketHandler);
            }
        });
        ChannelFuture channelFuture = bootstrap.bind().sync();
        log.info("Server started and listen on:{}",channelFuture.channel().localAddress());
        channelFuture.channel().closeFuture().sync();

    }

    @PreDestroy
    public void destroy() throws InterruptedException {
        if(mainGroup != null){
            mainGroup.shutdownGracefully().sync();
        }
        if(workGroup != null){
            workGroup.shutdownGracefully().sync();
        }
    }
    @PostConstruct()
    public void init() {
        //需要开启一个新的线程来执行netty server 服务器
        new Thread(() -> {
            try {
                start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }



}
