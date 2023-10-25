package com.todostudy.iot.mqtt.server.store.message;

import com.todostudy.iot.mqtt.server.common.message.DupPubRelMessageStore;
import com.todostudy.iot.mqtt.server.common.message.IDupPubRelMessageStoreService;
import com.todostudy.iot.mqtt.server.common.message.IMessageIdService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//PUBREL重发消息存储服务接口, 当QoS=2时存在该重发机制
public class DupPubRelMessageMemoryStoreService implements IDupPubRelMessageStoreService {


	private final IMessageIdService messageIdService;

	private Map<String, ConcurrentHashMap<Integer, DupPubRelMessageStore>> dupPubRelMessageCache = new HashMap<>();

	public DupPubRelMessageMemoryStoreService(IMessageIdService messageIdService) {
		this.messageIdService = messageIdService;
	}

	@Override
	public void put(String clientId, DupPubRelMessageStore dupPubRelMessageStore) {
		ConcurrentHashMap<Integer, DupPubRelMessageStore> map = dupPubRelMessageCache.containsKey(clientId) ? dupPubRelMessageCache.get(clientId) : new ConcurrentHashMap<Integer, DupPubRelMessageStore>();
		map.put(dupPubRelMessageStore.getMessageId(), dupPubRelMessageStore);
		dupPubRelMessageCache.put(clientId, map);
	}

	@Override
	public List<DupPubRelMessageStore> get(String clientId) {
		if (dupPubRelMessageCache.containsKey(clientId)) {
			ConcurrentHashMap<Integer, DupPubRelMessageStore> map = dupPubRelMessageCache.get(clientId);
			Collection<DupPubRelMessageStore> collection = map.values();
			return new ArrayList<DupPubRelMessageStore>(collection);
		}
		return new ArrayList<DupPubRelMessageStore>();
	}

	@Override
	public void remove(String clientId, int messageId) {
		if (dupPubRelMessageCache.containsKey(clientId)) {
			ConcurrentHashMap<Integer, DupPubRelMessageStore> map = dupPubRelMessageCache.get(clientId);
			if (map.containsKey(messageId)) {
				map.remove(messageId);
				if (map.size() > 0) {
					dupPubRelMessageCache.put(clientId, map);
				} else {
					dupPubRelMessageCache.remove(clientId);
				}
			}
		}
	}

	@Override
	public void removeByClient(String clientId) {
		if (dupPubRelMessageCache.containsKey(clientId)) {
			ConcurrentHashMap<Integer, DupPubRelMessageStore> map = dupPubRelMessageCache.get(clientId);
			map.forEach((messageId, dupPubRelMessageStore) -> {
				messageIdService.releaseMessageId(messageId);
			});
			map.clear();
			dupPubRelMessageCache.remove(clientId);
		}
	}
}

