package com.laich.test;

import com.todostudy.iot.mqtt.server.common.subscribe.SubscribeStore;
import com.todostudy.iot.mqtt.server.store.subscribe.SubscribeRedisStoreService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

//@SpringBootTest(classes = BrokerApplication.class)
//@RunWith(SpringRunner.class)
public class Tsmq {

    @Autowired
    SubscribeRedisStoreService subscribeStoreService;

        public void tsMq(){

        SubscribeStore subscribeStore = new SubscribeStore("laich","sys/laich/#",0);
        subscribeStoreService.put("sys/laich/#",subscribeStore);

        SubscribeStore subscribeStore2 = new SubscribeStore("test","sys/test/#",0);
        subscribeStoreService.put("sys/test/#",subscribeStore2);

        List<SubscribeStore> search = subscribeStoreService.search("sys/test/123");
        System.out.println(search);
    }
}
