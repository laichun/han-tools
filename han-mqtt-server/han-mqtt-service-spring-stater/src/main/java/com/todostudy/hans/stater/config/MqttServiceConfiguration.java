package com.todostudy.hans.stater.config;

import com.todostudy.iot.mqtt.server.MqttBrokerServer;
import com.todostudy.iot.mqtt.server.MqttServerCreator;
import com.todostudy.iot.mqtt.server.api.IAuthService;
import com.todostudy.iot.mqtt.server.api.ICheckSubscribeValidator;
import com.todostudy.iot.mqtt.server.api.IMqttListenConnect;
import com.todostudy.iot.mqtt.server.api.IMqttListenMessage;
import com.todostudy.iot.mqtt.server.common.Tools;
import com.todostudy.iot.mqtt.server.session.SessionStoreService;
import com.todostudy.iot.mqtt.server.store.message.MqttServerTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(value = {HanMqttBrokerProperties.class})
public class MqttServiceConfiguration {

    @Bean
    public MqttServerCreator mqttServerCreator(HanMqttBrokerProperties properties,
                                               ObjectProvider<IMqttListenConnect> iMqttListenConnectsProvider,
                                               ObjectProvider<IMqttListenMessage> iMqttListenMessagesProvider,
                                               ObjectProvider<IAuthService> authServicesProvider,
                                               ObjectProvider<ICheckSubscribeValidator> checkSubscribeValidatorsProvider,
                                               ObjectProvider<RedisTemplate> redisTemplatesProvider){

        MqttServerCreator mqttServerCreator = MqttBrokerServer.createServer().sslPort(properties.getSslPort())
                .sslAuth(properties.isSslAuth()).wsEnable(properties.isWsEnable())
                .cacheType(properties.getCacheType()).port(properties.getPort()).maxTransMessage(properties.getMaxTransMessage());

        //注入
        iMqttListenConnectsProvider.ifAvailable(mqttServerCreator::mqttListenConnect);
        iMqttListenMessagesProvider.ifAvailable(mqttServerCreator::mqttListenMessage);
        authServicesProvider.ifAvailable(mqttServerCreator::authService);
        checkSubscribeValidatorsProvider.ifAvailable(mqttServerCreator::checkSubscribeValidator);

        //ssl-config
        if(properties.isSslAuth()){
           mqttServerCreator.sslConfig(mqttServerCreator.builderSslConfig(properties.getSslConfig().isEnable()
           ,properties.getSslConfig().getKeystorePath(),properties.getSslConfig().getKeystorePwd(),
                   properties.getSslConfig().getTruststorePath(),properties.getSslConfig().getTruststorePwd()));
        }
        //检查如果使用redis 必须注入redisTemplates
        if(properties.getCacheType().equals(Tools.CACHE_REDIS)){
            if(redisTemplatesProvider==null){
                throw new RuntimeException(" redisTemplates is not null");
            }else{
                redisTemplatesProvider.ifAvailable(mqttServerCreator::redisTemplate);
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
    public MqttServerTemplate mqttServerTemplate(MqttBrokerServer mqttBrokerServer) {
        return new MqttServerTemplate(mqttBrokerServer.getMqttServerTemplateProcessor());
    }


}
