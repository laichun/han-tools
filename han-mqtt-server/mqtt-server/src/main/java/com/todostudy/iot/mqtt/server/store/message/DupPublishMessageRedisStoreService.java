/**
 * 
 */

package com.todostudy.iot.mqtt.server.store.message;

import com.todostudy.iot.mqtt.server.common.message.DupPublishMessageStore;
import com.todostudy.iot.mqtt.server.common.message.IDupPublishMessageStoreService;
import com.todostudy.iot.mqtt.server.common.message.IMessageIdService;
import com.todostudy.iot.mqtt.server.store.cache.RedisServices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

//PUBLISH重发消息存储服务接口, 当QoS=1和QoS=2时存在该重发机制
public class DupPublishMessageRedisStoreService implements IDupPublishMessageStoreService {


	private final IMessageIdService messageIdService;

	private final RedisServices redisServices;

	public DupPublishMessageRedisStoreService(IMessageIdService messageIdService, RedisServices redisServices) {
		this.messageIdService = messageIdService;
		this.redisServices = redisServices;
	}

	@Override
	public void put(String clientId, DupPublishMessageStore dupPublishMessageStore) {
		ConcurrentHashMap<String, DupPublishMessageStore> map = redisServices.getCacheObject(clientId)!=null ? (ConcurrentHashMap<String, DupPublishMessageStore>) redisServices.getCacheObject(clientId) : new ConcurrentHashMap<String, DupPublishMessageStore>();
		map.put(dupPublishMessageStore.getMessageId()+"", dupPublishMessageStore);
		//dupPublishMessageCache.put(clientId, map);
		redisServices.setCacheObject(clientId, map);

	}

	@Override
	public List<DupPublishMessageStore> get(String clientId) {
		//if (dupPublishMessageCache.containsKey(clientId)) {
		if (redisServices.getCacheObject(clientId)!=null) {
			//ConcurrentHashMap<Integer, DupPublishMessageStore> map = dupPublishMessageCache.get(clientId);
			ConcurrentHashMap<String, DupPublishMessageStore> map = (ConcurrentHashMap<String, DupPublishMessageStore>) redisServices.getCacheObject(clientId);
			Collection<DupPublishMessageStore> collection = map.values();
			return new ArrayList<DupPublishMessageStore>(collection);
		}
		return new ArrayList<DupPublishMessageStore>();
	}

	@Override
	public void remove(String clientId, int messageId) {
		//if (dupPublishMessageCache.containsKey(clientId)) {
		if (redisServices.getCacheObject(clientId)!=null) {
			//ConcurrentHashMap<Integer, DupPublishMessageStore> map = dupPublishMessageCache.get(clientId);
			ConcurrentHashMap<String, DupPublishMessageStore> map = (ConcurrentHashMap<String, DupPublishMessageStore>) redisServices.getCacheObject(clientId);
			if (map.containsKey(messageId)) {
				map.remove(messageId);
				if (map.size() > 0) {
					//dupPublishMessageCache.put(clientId, map);
					redisServices.setCacheObject(clientId, map);
				} else {
					//dupPublishMessageCache.remove(clientId);
					redisServices.delete(clientId);
				}
			}
		}
	}

	@Override
	public void removeByClient(String clientId) {
		//if (dupPublishMessageCache.containsKey(clientId)) {
		if (redisServices.getCacheObject(clientId)!=null) {
			//ConcurrentHashMap<Integer, DupPublishMessageStore> map = dupPublishMessageCache.get(clientId);
			ConcurrentHashMap<String, DupPublishMessageStore> map = (ConcurrentHashMap<String, DupPublishMessageStore>) redisServices.getCacheObject(clientId);
			map.forEach((messageId, dupPublishMessageStore) -> {
				messageIdService.releaseMessageId(dupPublishMessageStore.getMessageId());
			});
			map.clear();
			//dupPublishMessageCache.remove(clientId);
			redisServices.delete(clientId);
		}
	}
}
