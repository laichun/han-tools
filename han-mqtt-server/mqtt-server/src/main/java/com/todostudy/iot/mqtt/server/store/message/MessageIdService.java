package com.todostudy.iot.mqtt.server.store.message;


import com.todostudy.iot.mqtt.server.common.message.IMessageIdService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageIdService implements IMessageIdService {
    /**
     * messageId 存储 clientId: messageId
     */
    private final Map<String, AtomicInteger> messageIdStore = new ConcurrentHashMap<>();

    @Override
    public int getMessageId(String clientId) {
        AtomicInteger value = messageIdStore.computeIfAbsent(clientId, (key) -> new AtomicInteger(1));
        value.compareAndSet(0xffff, 1);
        return value.getAndIncrement();
    }
}
