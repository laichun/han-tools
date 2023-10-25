package com.han.mqtt.server.example.service;

import cn.hutool.core.util.StrUtil;
import com.todostudy.iot.mqtt.server.api.ICheckSubscribeValidator;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MySubscribeValidator  implements ICheckSubscribeValidator {
    @Override
    public boolean subscribeValidator(String clientId, List<MqttTopicSubscription> topicSubscriptions) {
        for (MqttTopicSubscription topicSubscription : topicSubscriptions) {
            String topicFilter = topicSubscription.topicName();
            // 以#或+符号开头的、以/符号结尾的及不存在/符号的订阅按非法订阅处理,
            if (StrUtil.startWith(topicFilter, '#') || StrUtil.startWith(topicFilter, '+') || StrUtil.endWith(topicFilter, '/') || !StrUtil.contains(topicFilter, '/'))
                return false;
        }
        return true;
    }
}
