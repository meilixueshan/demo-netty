package com.example.demo3.server;

import io.netty.channel.ChannelId;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Optional;

@Slf4j
public class ClientChannel {
    private static HashMap<String, ChannelId> clientMap = new HashMap<>();

    public static synchronized void put(String userId, ChannelId channelId) {
        clientMap.put(userId, channelId);
    }

    public static synchronized Optional<ChannelId> get(String userId) {
        if (clientMap.containsKey(userId)) {
            return Optional.ofNullable(clientMap.get(userId));
        }
        return Optional.empty();
    }

    public static synchronized void remove(String userId) {
        if (clientMap.containsKey(userId)) {
            clientMap.remove(userId);
        }
    }

    public static synchronized void output() {
        for (HashMap.Entry<String, ChannelId> entry : clientMap.entrySet()) {
            log.debug("UserId: {}, ChannelId: {}", entry.getKey(), entry.getValue().asLongText());
        }
    }
}