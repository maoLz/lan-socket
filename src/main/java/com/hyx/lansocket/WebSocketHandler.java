package com.hyx.lansocket;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Date;

@Slf4j
@Component
@ChannelHandler.Sharable
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("[WebSocketHandler#channelRead0]{}",ctx.channel().id().asLongText());
        NettyConfig.getChannels().add(ctx.channel());
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        log.info("服务器收到消息：{}",msg.text());
        SocketMsg socketMsg = JSONUtil.toBean(msg.text(), SocketMsg.class);
        String uid = socketMsg.getUid();
        if("connecting".equals(socketMsg.getData())) {
            // 将用户ID作为自定义属性加入到channel中，方便随时channel中获取用户ID

            AttributeKey<String> key = AttributeKey.valueOf("userId");
            ctx.channel().attr(key).setIfAbsent(socketMsg.getUid());
            NettyConfig.getChannelMap().put(uid,ctx.channel());
            returnMsg(ctx.channel(),"connect success");
            return;
        }
        InetSocketAddress address = (InetSocketAddress) ctx.pipeline().channel().remoteAddress();
        socketMsg.setIp(address.getHostString());
        socketMsg.setDate(new Date());
        MsgQueue.addMsg(socketMsg);
        sendAllUserData(socketMsg);
        returnMsg(ctx.channel(), "send success");

        // 回复消息
    }



    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        log.info("handlerRemoved 被调用"+ctx.channel().id().asLongText());
        // 删除通道
        NettyConfig.getChannels().remove(ctx.channel());
        removeUserId(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("异常：{}",cause.getMessage());
        // 删除通道
        NettyConfig.getChannels().remove(ctx.channel());
        removeUserId(ctx);
        ctx.close();
    }

    /**
     * 删除用户与channel的对应关系
     * @param ctx
     */
    private void removeUserId(ChannelHandlerContext ctx){
        AttributeKey<String> key = AttributeKey.valueOf("userId");
        String userId = ctx.channel().attr(key).get();
        NettyConfig.getChannels().remove(userId);
    }

    public static void returnMsg(Channel channel, String msg){
        returnMsg(channel,"1",msg);
    }

    public static void sendAllUserData(SocketMsg socketMsg){
        for(Channel channel:NettyConfig.getChannels()){
            sendData(channel,socketMsg);
        }
    }

    public static void sendData(Channel channel,SocketMsg socketMsg){
        JSONObject json = new JSONObject();
        json.put("type","2");
        json.put("data",socketMsg);
        channel.writeAndFlush(new TextWebSocketFrame(json.toJSONString()));
    }


    private static void returnMsg(Channel channel,String type, String data){
        JSONObject json = new JSONObject();
        json.put("type",type);
        json.put("data",data);
        channel.writeAndFlush(new TextWebSocketFrame(json.toJSONString()));
    }




}
