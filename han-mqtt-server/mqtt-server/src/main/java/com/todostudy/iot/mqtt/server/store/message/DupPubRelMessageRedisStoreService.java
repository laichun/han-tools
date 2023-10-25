/**
 * 
 */

package com.todostudy.iot.mqtt.server.store.message;

import com.todostudy.iot.mqtt.server.common.message.DupPubRelMessageStore;
import com.todostudy.iot.mqtt.server.common.message.IDupPubRelMessageStoreService;
import com.todostudy.iot.mqtt.server.store.cache.RedisServices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

//PUBREL重发消息存储服务接口, 当QoS=2时存在该重发机制
//集群使用 redis
public class DupPubRelMessageRedisStoreService implements IDupPubRelMessageStoreService {

	private final RedisServices redisServices;

	public DupPubRelMessageRedisStoreService(RedisServices redisServices) {
		this.redisServices = redisServices;
	}

	@Override
	public void put(String clientId, DupPubRelMessageStore dupPubRelMessageStore) {
		//ConcurrentHashMap<Integer, DupPubRelMessageStore> map = dupPubRelMessageCache.containsKey(clientId) ? dupPubRelMessageCache.get(clientId) : new ConcurrentHashMap<Integer, DupPubRelMessageStore>();
		ConcurrentHashMap<Integer, DupPubRelMessageStore> map = redisServices.getCacheObject(clientId)!=null ? (ConcurrentHashMap<Integer, DupPubRelMessageStore>) redisServices.getCacheObject(clientId) : new ConcurrentHashMap<Integer, DupPubRelMessageStore>();
		map.put(dupPubRelMessageStore.getMessageId(), dupPubRelMessageStore);
		/*dupPubRelMessageCache.put(clientId, map);*/
		redisServices.setCacheObject(clientId, map);
	}

	@Override
	public List<DupPubRelMessageStore> get(String clientId) {
		if (redisServices.getCacheObject(clientId)!=null) {
			ConcurrentHashMap<Integer, DupPubRelMessageStore> map = (ConcurrentHashMap<Integer, DupPubRelMessageStore>) redisServices.getCacheObject(clientId);
			Collection<DupPubRelMessageStore> collection = map.values();
			return new ArrayList<DupPubRelMessageStore>(collection);
		}
		return new ArrayList<DupPubRelMessageStore>();
	}

	@Override
	public void remove(String clientId, int messageId) {
		if (redisServices.getCacheObject(clientId)!=null) {
			ConcurrentHashMap<Integer, DupPubRelMessageStore> map = (ConcurrentHashMap<Integer, DupPubRelMessageStore>) redisServices.getCacheObject(clientId);
			if (map.containsKey(messageId)) {
				map.remove(messageId);
				if (map.size() > 0) {
					redisServices.setCacheObject(clientId, map);

				} else {
					redisServices.delete(clientId);
				}
			}
		}
	}

	@Override
	public void removeByClient(String clientId) {
		if (redisServices.getCacheObject(clientId)!=null) {
			ConcurrentHashMap<Integer, DupPubRelMessageStore> map = (ConcurrentHashMap<Integer, DupPubRelMessageStore>) redisServices.getCacheObject(clientId);
			map.forEach((messageId, dupPubRelMessageStore) -> {
				//messageIdService.releaseMessageId(messageId);
			});
			map.clear();
			redisServices.delete(clientId);
		}
	}
}
