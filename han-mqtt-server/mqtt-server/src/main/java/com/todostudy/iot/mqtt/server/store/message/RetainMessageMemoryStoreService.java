package com.todostudy.iot.mqtt.server.store.message;

import cn.hutool.core.util.StrUtil;
import com.todostudy.iot.mqtt.server.common.message.IRetainMessageStoreService;
import com.todostudy.iot.mqtt.server.common.message.RetainMessageStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RetainMessageMemoryStoreService implements IRetainMessageStoreService {

    private Map<String, RetainMessageStore> retainMessageCache = new ConcurrentHashMap<>();;

    public void put(String topic, RetainMessageStore retainMessageStore) {
        retainMessageCache.put(topic, retainMessageStore);
    }

    @Override
    public RetainMessageStore get(String topic) {
        return retainMessageCache.get(topic);
    }

    @Override
    public void remove(String topic) {
        retainMessageCache.remove(topic);
    }

    @Override
    public boolean containsKey(String topic) {
        return retainMessageCache.containsKey(topic);
    }

    @Override
    public List<RetainMessageStore> search(String topicFilter) {
        List<RetainMessageStore> retainMessageStores = new ArrayList<RetainMessageStore>();
        if (!StrUtil.contains(topicFilter, '#') && !StrUtil.contains(topicFilter, '+')) {
            if (retainMessageCache.containsKey(topicFilter)) {
                retainMessageStores.add(retainMessageCache.get(topicFilter));
            }
        } else {
            retainMessageCache.forEach((key,val) -> {
                String topic = key;
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
                        RetainMessageStore retainMessageStore = val;
                        retainMessageStores.add(retainMessageStore);
                    }
                }
            });
        }
        return retainMessageStores;
    }
}
