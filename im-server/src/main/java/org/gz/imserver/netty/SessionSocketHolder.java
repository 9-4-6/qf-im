package org.gz.imserver.netty;

import io.netty.channel.Channel;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: guohzhong
 */
public class SessionSocketHolder {

    private static final Map<Long, Channel> CHANNELS = new ConcurrentHashMap<>();

    public static void put(Long userId, Channel channel){

        CHANNELS.put(userId,channel);
    }

    public static Channel get(Long userId){

        return CHANNELS.get(userId);
    }


    public static void remove(Long userId){
        CHANNELS.remove(userId);
    }

}
