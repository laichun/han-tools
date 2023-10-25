package com.todostudy.iot.mqtt.server.store.message;

import com.todostudy.iot.mqtt.server.common.message.DupPublishMessageStore;
import com.todostudy.iot.mqtt.server.common.message.IDupPublishMessageStoreService;
import com.todostudy.iot.mqtt.server.common.message.IMessageIdService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息存储了，其实不用存储，发到业务接口上去
 * PUBLISH重发消息存储服务接口, 当QoS=1和QoS=2时存在该重发机制
 */
public class DupPublishMessageMemoryStoreService implements IDupPublishMessageStoreService {

    private final IMessageIdService messageIdService ;

    private Map<String, ConcurrentHashMap<Integer, DupPublishMessageStore>> dupPublishMessageCache = new HashMap<>();

    public DupPublishMessageMemoryStoreService(IMessageIdService messageIdService) {
        this.messageIdService = messageIdService;
    }

    @Override
    public void put(String clientId, DupPublishMessageStore dupPublishMessageStore) {
        ConcurrentHashMap<Integer, DupPublishMessageStore> map = dupPublishMessageCache.containsKey(clientId) ? dupPublishMessageCache.get(clientId) : new ConcurrentHashMap<Integer, DupPublishMessageStore>();
        map.put(dupPublishMessageStore.getMessageId(), dupPublishMessageStore);
        dupPublishMessageCache.put(clientId, map);
    }

    @Override
    public List<DupPublishMessageStore> get(String clientId) {
        if (dupPublishMessageCache.containsKey(clientId)) {
            ConcurrentHashMap<Integer, DupPublishMessageStore> map = dupPublishMessageCache.get(clientId);
            Collection<DupPublishMessageStore> collection = map.values();
            return new ArrayList<DupPublishMessageStore>(collection);
        }
        return new ArrayList<DupPublishMessageStore>();
    }

    @Override
    public void remove(String clientId, int messageId) {
        if (dupPublishMessageCache.containsKey(clientId)) {
            ConcurrentHashMap<Integer, DupPublishMessageStore> map = dupPublishMessageCache.get(clientId);
            if (map.containsKey(messageId)) {
                map.remove(messageId);
                if (map.size() > 0) {
                    dupPublishMessageCache.put(clientId, map);
                } else {
                    dupPublishMessageCache.remove(clientId);
                }
            }
        }
    }

    @Override
    public void removeByClient(String clientId) {
        if (dupPublishMessageCache.containsKey(clientId)) {
            ConcurrentHashMap<Integer, DupPublishMessageStore> map = dupPublishMessageCache.get(clientId);
            map.forEach((messageId, dupPublishMessageStore) -> {
                messageIdService.releaseMessageId(messageId);
            });
            map.clear();
            dupPublishMessageCache.remove(clientId);
        }
    }
}