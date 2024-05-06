package com.todostudy.hans.stater.config;

import com.todostudy.iot.mqtt.server.MqttBrokerServer;
import com.todostudy.iot.mqtt.server.MqttServerCreator;
import com.todostudy.iot.mqtt.server.api.*;
import com.todostudy.iot.mqtt.server.common.Tools;
import com.todostudy.iot.mqtt.server.protocol.WebSocketServerProcessor;
import com.todostudy.iot.mqtt.server.session.SessionStoreService;
import com.todostudy.iot.mqtt.server.store.message.MqttServerTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
/**
 * @Author: hanson
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(value = {HanMqttBrokerProperties.class})
public class MqttServiceConfiguration {


    @Bean
    public MqttServerCreator mqttServerCreator(HanMqttBrokerProperties properties,
                                               ObjectProvider<IMqttListenConnect> iMqttListenConnectsProvider,
                                               ObjectProvider<IMqttListenMessage> iMqttListenMessagesProvider,
                                               ObjectProvider<IAuthService> authServicesProvider,
                                               ObjectProvider<IWebSocketService> webSocketServices,
                                               ObjectProvider<ICheckSubscribeValidator> checkSubscribeValidatorsProvider,
                                               ObjectProvider<RedisTemplate> redisTemplate){

        MqttServerCreator mqttServerCreator = MqttBrokerServer.createServer().port(properties.getPort())
                .sslAuth(properties.isSslAuth()).wsEnable(properties.isWsEnable()).retainMsgTime(properties.getRetainMsgTime())
                .cacheType(properties.getCacheType()).port(properties.getPort()).maxTransMessage(properties.getMaxTransMessage());

        //注入
        iMqttListenConnectsProvider.ifAvailable(mqttServerCreator::mqttListenConnect);
        iMqttListenMessagesProvider.ifAvailable(mqttServerCreator::mqttListenMessage);
        authServicesProvider.ifAvailable(mqttServerCreator::authService);
        checkSubscribeValidatorsProvider.ifAvailable(mqttServerCreator::checkSubscribeValidator);
        /**
         * ssl-config ,支持单向和双休认证
         */
        if(properties.isSslAuth()){
           mqttServerCreator.sslConfig(mqttServerCreator.builderSslConfig(properties.getSslConfig().isTwoWay(),properties.getSslConfig().getSslPort(),
                   properties.getSslConfig().isSslUserAuth(),properties.getSslConfig().getKeystorePath(),properties.getSslConfig().getKeystorePwd(),
                   properties.getSslConfig().getTwoWayCerChainFile(),properties.getSslConfig().getTwoWayKeyFile(),properties.getSslConfig().getTwoWayRootFile()));
        }
        //检查如果使用redis 必须注入 redisTemplate
        if(properties.getCacheType().equals(Tools.CACHE_REDIS)){
            redisTemplate.ifAvailable(mqttServerCreator::redisTemplate);
        }
        //ws配置
        if(properties.isWsEnable()){
            mqttServerCreator.wsEnableSsl(properties.isWsEnableSsl());
            mqttServerCreator.wsModel(properties.getWsModel());
            mqttServerCreator.websocketPath(properties.getWebsocketPath());
            mqttServerCreator.websocketSslPort(properties.getWebsocketSslPort());
            if(properties.getWsModel()==2){
                webSocketServices.ifAvailable(mqttServerCreator::iWebSocketService);
            }
        }

        return mqttServerCreator;
    }

    @Bean
    public SessionStoreService sessionStoreService() {
        return new SessionStoreService();
    }
    @Bean
    public MqttBrokerServer mqttBrokerServer(MqttServerCreator mqttServerCreator,SessionStoreService sessionStoreService) {
        MqttBrokerServer mqttBrokerServer = mqttServerCreator.build(sessionStoreService);
        return mqttBrokerServer;
    }

    @Bean
    public MqttLifecycleLauncher mqttServerLauncher(MqttBrokerServer mqttBrokerServer) {
        return new MqttLifecycleLauncher(mqttBrokerServer);
    }
    @Bean
    @Lazy
    public MqttServerTemplate mqttServerTemplate(MqttBrokerServer mqttBrokerServer) {
        return new MqttServerTemplate(mqttBrokerServer.getMqttServerTemplateProcessor());
    }

    @Bean
    @Lazy
    public WebSocketServerProcessor webSocketServerProcessor(MqttBrokerServer mqttBrokerServer) {
        return mqttBrokerServer.getWebSocketServerProcessor();
    }

}
