package com.todostudy.iot.mqtt.server.store.message;


import com.todostudy.iot.mqtt.server.common.message.IMessageIdService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageIdService implements IMessageIdService {

    private final int MIN_MSG_ID = 1;
    //使用AtomicInteger 保证原子性
    private AtomicInteger count = new AtomicInteger(1);
    private final int MAX_MSG_ID = 65535;

    private final int lock = 0;

    private ConcurrentHashMap<Integer, Integer> messageIdCache = new ConcurrentHashMap<>();

    private int nextMsgId = MIN_MSG_ID - 1;

    public int increment() {
        return count.incrementAndGet();
    }

    @Override
    public int getNextMessageId() {
        try {
            do {
                nextMsgId=increment();
                if (nextMsgId > MAX_MSG_ID) {
                    nextMsgId = MIN_MSG_ID;
                }
            } while (messageIdCache.containsKey(nextMsgId));
            messageIdCache.put(nextMsgId, nextMsgId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return nextMsgId;
    }

    @Override
    public void releaseMessageId(int messageId) {
        try {
            messageIdCache.remove(messageId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
}
