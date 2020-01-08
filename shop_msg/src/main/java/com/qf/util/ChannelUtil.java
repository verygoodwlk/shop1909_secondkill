package com.qf.util;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelUtil {

    private static Map<Integer, Channel> map = new ConcurrentHashMap<>();

    public static void add(Integer uid, Channel channel){
        map.put(uid, channel);
    }

    public static Channel getChannel(Integer uid){
        return map.get(uid);
    }

    public static void removeChannel(Integer uid){
        map.remove(uid);
    }

    public static Integer getUid(Channel channel){
        for (Map.Entry<Integer, Channel> entry : map.entrySet()) {
            if(entry.getValue() == channel){
                return entry.getKey();
            }
        }
        return null;
    }
}
