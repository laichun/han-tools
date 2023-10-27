/**
 *
 */

package com.todostudy.iot.mqtt.server.store.message;

import cn.hutool.core.util.StrUtil;
import com.todostudy.iot.mqtt.server.common.message.IRetainMessageStoreService;
import com.todostudy.iot.mqtt.server.common.message.RetainMessageStore;
import com.todostudy.iot.mqtt.server.store.cache.RedisServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class RetainMessageRedisStoreService implements IRetainMessageStoreService {


    private final RedisServices redisServices;

    public RetainMessageRedisStoreService(RedisServices redisServices) {
        this.redisServices = redisServices;
    }

    @Override
    public void put(String topic, RetainMessageStore retainMessageStore) {
        redisServices.setCacheObjectRetainMsg(RedisServices.MQTT_RETAIN + topic, retainMessageStore);
    }

    @Override
    public RetainMessageStore get(String topic) {
        return (RetainMessageStore) redisServices.getCacheObject(RedisServices.MQTT_RETAIN+topic);
    }

    @Override
    public void remove(String topic) {
        redisServices.delete(RedisServices.MQTT_RETAIN+topic);
    }

    @Override
    public boolean containsKey(String topic) {
        return redisServices.getCacheObject(RedisServices.MQTT_RETAIN+topic) != null;
    }

    @Override
    public List<RetainMessageStore> search(String topicFilter) {
        List<RetainMessageStore> retainMessageStores = new ArrayList<RetainMessageStore>();
        if (!StrUtil.contains(topicFilter, '#') && !StrUtil.contains(topicFilter, '+')) {
            if (redisServices.getCacheObject(RedisServices.MQTT_RETAIN + topicFilter) != null) {
                retainMessageStores.add((RetainMessageStore) redisServices.getCacheObject(RedisServices.MQTT_RETAIN + topicFilter));
            }
        } else {
            //get all key
            Set<String> keys = redisServices.getKeys(RedisServices.MQTT_RETAIN + "*");
            keys.forEach(item -> {
                String topic = item.replace(RedisServices.MQTT_RETAIN, "");
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
                        RetainMessageStore retainMessageStore = (RetainMessageStore) redisServices.getCacheObject(RedisServices.MQTT_RETAIN + topic);
                        retainMessageStores.add(retainMessageStore);
                    }
                }
            });
        }
        return retainMessageStores;
    }
}
