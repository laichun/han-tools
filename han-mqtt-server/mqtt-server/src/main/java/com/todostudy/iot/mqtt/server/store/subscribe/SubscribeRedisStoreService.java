/**
 * 
 */

package com.todostudy.iot.mqtt.server.store.subscribe;

import cn.hutool.core.util.StrUtil;
import com.todostudy.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import com.todostudy.iot.mqtt.server.common.subscribe.SubscribeStore;
import com.todostudy.iot.mqtt.server.store.cache.RedisServices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 订阅存储服务， 集群时候使用 + reids
 */
public class SubscribeRedisStoreService implements ISubscribeStoreService {

	final static String SUBSCRIBE_TOPIC="mqtt:subscribe:";
	final static String SUBSCRIBE_TOPIC_N="mqtt:subscribe_n:";
	private final RedisServices redisServices;

	public SubscribeRedisStoreService(RedisServices redisServices) {
		this.redisServices = redisServices;
	}

	/**
	 * 为了快速查出数据，存储key 分有通配符合无通配符
	 * @param topicFilter
	 * @param subscribeStore
	 */
	@Override
	public void put(String topicFilter, SubscribeStore subscribeStore) {
		if (StrUtil.contains(topicFilter, '#') || StrUtil.contains(topicFilter, '+')) {
			topicFilter = SUBSCRIBE_TOPIC+topicFilter;
			ConcurrentHashMap<String, SubscribeStore> map =
					redisServices.getCacheObject(topicFilter)!=null ? (ConcurrentHashMap<String, SubscribeStore>) redisServices.getCacheObject(topicFilter) : new ConcurrentHashMap<String, SubscribeStore>();
			map.put(subscribeStore.getClientId(), subscribeStore);
			redisServices.setCacheObject(topicFilter, map);
			//redisTemplate.opsForHash().putAll(topicFilter,map);
		} else {
			topicFilter = SUBSCRIBE_TOPIC_N+topicFilter;
			ConcurrentHashMap<String, SubscribeStore> map =
					redisServices.getCacheObject(topicFilter)!=null ? (ConcurrentHashMap<String, SubscribeStore>) redisServices.getCacheObject(topicFilter) : new ConcurrentHashMap<String, SubscribeStore>();
			map.put(subscribeStore.getClientId(), subscribeStore);
			redisServices.setCacheObject(topicFilter,map);
		}
	}

	@Override
	public void remove(String topicFilter, String clientId) {
		if (StrUtil.contains(topicFilter, '#') || StrUtil.contains(topicFilter, '+')) {
			topicFilter = SUBSCRIBE_TOPIC+topicFilter;
			if (redisServices.getCacheObject(topicFilter)!=null) {
				ConcurrentHashMap<String, SubscribeStore> map = (ConcurrentHashMap<String, SubscribeStore>) redisServices.getCacheObject(topicFilter);
				if (map.containsKey(clientId)) {
					map.remove(clientId);
					if (map.size() > 0) {
						redisServices.setCacheObject(topicFilter, map);
					} else {
						redisServices.delete(topicFilter);
					}
				}
			}
		} else {
			topicFilter = SUBSCRIBE_TOPIC_N+topicFilter;
			if (redisServices.getCacheObject(topicFilter)!=null) {
				ConcurrentHashMap<String, SubscribeStore> map = (ConcurrentHashMap<String, SubscribeStore>) redisServices.getCacheObject(topicFilter);
				if (map.containsKey(clientId)) {
					map.remove(clientId);
					if (map.size() > 0) {
						redisServices.setCacheObject(topicFilter, map);
					} else {
						redisServices.delete(topicFilter);
					}
				}
			}
		}
	}

	@Override
	public void removeForClient(String clientId) {
		Set<String> keys = redisServices.getKeys(SUBSCRIBE_TOPIC + "*");
		keys.forEach(item->{
			ConcurrentHashMap<String, SubscribeStore> map= (ConcurrentHashMap<String, SubscribeStore>) redisServices.getCacheObject(item);
			if (map.containsKey(clientId)) {
				map.remove(clientId);
				if (map.size() > 0) {
					redisServices.setCacheObject(item, map);
				} else {
					redisServices.delete(item);
				}
			}

		});

		Set<String> keysNotWild = redisServices.getKeys(SUBSCRIBE_TOPIC_N + "*");
		keysNotWild.forEach(item->{
			ConcurrentHashMap<String, SubscribeStore> map= (ConcurrentHashMap<String, SubscribeStore>) redisServices.getCacheObject(item);
			if (map.containsKey(clientId)) {
				map.remove(clientId);
				if (map.size() > 0) {
					redisServices.setCacheObject(item, map);
				} else {
					redisServices.delete(item);
				}
			}

		});

	}

	@Override
	public List<SubscribeStore> search(String topic) {
		List<SubscribeStore> subscribeStores = new ArrayList<SubscribeStore>();
		if (redisServices.getCacheObject(SUBSCRIBE_TOPIC_N + topic)!=null) { //无通配符
			ConcurrentHashMap<String, SubscribeStore> map = (ConcurrentHashMap<String, SubscribeStore>) redisServices.getCacheObject(SUBSCRIBE_TOPIC_N + topic);
			Collection<SubscribeStore> collection = map.values();
			List<SubscribeStore> list = new ArrayList<SubscribeStore>(collection);
			subscribeStores.addAll(list);
		}

		Set<String> keys = redisServices.getKeys(SUBSCRIBE_TOPIC + "*");
		keys.forEach(item->{
			String topicFilter = item.replace(SUBSCRIBE_TOPIC,"");
			if (StrUtil.split(topic, '/').size() >= StrUtil.split(topicFilter, '/').size()) {
				List<String> splitTopics = StrUtil.split(topic, '/');
				List<String> spliteTopicFilters = StrUtil.split(topicFilter, '/');
				String newTopicFilter = "";
				for (int i = 0; i < spliteTopicFilters.size(); i++) {
					String value = spliteTopicFilters.get(i);
					if (value.equals("+")) {
						newTopicFilter = newTopicFilter + "+/";
					} else if (value.equals("#")) {
						newTopicFilter = newTopicFilter + "#/";
						break;
					} else {
						newTopicFilter = newTopicFilter + splitTopics.get(i) + "/";
					}
				}
				newTopicFilter = StrUtil.removeSuffix(newTopicFilter, "/");
				if (topicFilter.equals(newTopicFilter)) {
					ConcurrentHashMap<String, SubscribeStore> map = (ConcurrentHashMap<String, SubscribeStore>) redisServices.getCacheObject(item);//entry.getValue();
					Collection<SubscribeStore> collection = map.values();
					List<SubscribeStore> list = new ArrayList<SubscribeStore>(collection);
					subscribeStores.addAll(list);
				}
			}
		});
		return subscribeStores;
	}

	public static void main(String[] args) {


	}



}
