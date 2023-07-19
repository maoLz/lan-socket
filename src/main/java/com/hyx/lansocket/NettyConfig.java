package com.hyx.lansocket;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class NettyConfig {

    private static ChannelGroup channels =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private static Map<String, Channel> channelMap =
            new HashMap<>();


    public static ChannelGroup getChannels() {
        return channels;
    }

    public static Map<String, Channel> getChannelMap() {
        return channelMap;
    }
}
