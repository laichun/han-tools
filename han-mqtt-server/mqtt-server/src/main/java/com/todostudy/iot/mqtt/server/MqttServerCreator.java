package com.todostudy.iot.mqtt.server;


import com.todostudy.iot.mqtt.server.api.IAuthService;
import com.todostudy.iot.mqtt.server.api.ICheckSubscribeValidator;
import com.todostudy.iot.mqtt.server.api.IMqttListenConnect;
import com.todostudy.iot.mqtt.server.api.IMqttListenMessage;
import com.todostudy.iot.mqtt.server.common.Tools;
import com.todostudy.iot.mqtt.server.common.message.IDupPubRelMessageStoreService;
import com.todostudy.iot.mqtt.server.common.message.IDupPublishMessageStoreService;
import com.todostudy.iot.mqtt.server.common.message.IMessageIdService;
import com.todostudy.iot.mqtt.server.common.message.IRetainMessageStoreService;
import com.todostudy.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import com.todostudy.iot.mqtt.server.handler.MqttBrokerHandler;
import com.todostudy.iot.mqtt.server.protocol.MqttServerProcessor;
import com.todostudy.iot.mqtt.server.protocol.MqttServerTemplateProcessor;
import com.todostudy.iot.mqtt.server.session.SessionStoreService;
import com.todostudy.iot.mqtt.server.store.cache.RedisServices;
import com.todostudy.iot.mqtt.server.store.message.*;
import com.todostudy.iot.mqtt.server.store.subscribe.SubscribeRedisStoreService;
import com.todostudy.iot.mqtt.server.store.subscribe.SubscribeStoreMemoryService;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.redis.core.RedisTemplate;

@Getter
public class MqttServerCreator {

    /**
     * 默认内存缓存
     */
    private String cacheType = Tools.CACHE_MEMORY;
    /**
     * 是否开启Epoll模式, 默认关闭
     */
    private boolean useEpoll = false;
    /**
     * 默认不开启ssl认证
     */
    private boolean sslAuth;

    private SslConfig sslConfig;

    private boolean wsEnable;
    /**
     * WebSocket SSL端口号, 默认9993端口
     */
    private int websocketSslPort = 9995;

    /**
     * 默认2m的传输大小。单位 M
     */
    private int maxTransMessage = 2;

    /**
     * WebSocket Path值, 默认值 /mqtt
     */
    private String websocketPath = "/mqtt";
    /**
     * 心跳时间(秒), 默认60秒, 该值可被客户端连接时相应配置覆盖
     */
    private int keepAlive = 60;
    /**
     * Socket参数, 是否开启心跳保活机制, 默认开启
     */
    private boolean soKeepAlive = true;
    /**
     * Sokcet参数, 存放已完成三次握手请求的队列最大长度, 默认511长度
     */
    private int soBacklog = 511;
    /**
     * 端口
     */
    private int port = 1883;

    /**
     * Retain 消息过期时间
     */
    private long retainMsgTime; //小时

    /**
     * 心跳超时时间(单位: 毫秒 默认: 1000 * 120)，如果用户不希望框架层面做心跳相关工作，请把此值设为0或负数
     */
    private Long heartbeatTimeout;

    public ISubscribeStoreService subscribeStoreService;

    private IAuthService authService;

    private IMessageIdService messageIdService;

    public IRetainMessageStoreService retainMessageStoreService;

    public IDupPublishMessageStoreService dupPublishMessageStoreService;

    public IDupPubRelMessageStoreService dupPubRelMessageStoreService;

    private IMqttListenMessage mqttListenMessage;

    private IMqttListenConnect mqttListenConnect;

    private ICheckSubscribeValidator checkSubscribeValidator;
    private RedisTemplate redisTemplate;
    private RedisServices redisServices;


    public MqttBrokerServer build(SessionStoreService sessionStoreService) {
        //内部bean的初始化
        messageIdService = new MessageIdService();
        dupPublishMessageStoreService = new DupPublishMessageMemoryStoreService(messageIdService);
        dupPubRelMessageStoreService = new DupPubRelMessageMemoryStoreService(messageIdService);
        if (getCacheType().equals(Tools.CACHE_MEMORY)) {
            retainMessageStoreService = new RetainMessageMemoryStoreService();
            subscribeStoreService = new SubscribeStoreMemoryService();

        } else if (getCacheType().equals(Tools.CACHE_REDIS)) {
            redisServices = new RedisServices(redisTemplate, retainMsgTime);
            retainMessageStoreService = new RetainMessageRedisStoreService(redisServices);
            subscribeStoreService = new SubscribeRedisStoreService(redisServices);
        }
        MqttServerProcessor mqttServerProcessor = new MqttServerProcessor(sessionStoreService, subscribeStoreService, authService,
                messageIdService, retainMessageStoreService, dupPublishMessageStoreService,
                dupPubRelMessageStoreService, mqttListenMessage, mqttListenConnect, checkSubscribeValidator);
        MqttBrokerHandler mqttBrokerHandler = new MqttBrokerHandler(mqttServerProcessor);
        MqttServerTemplateProcessor mqttServerTemplateProcessor = new MqttServerTemplateProcessor(sessionStoreService, subscribeStoreService);
        // MqttServer
        MqttBrokerServer thisMqttServer = new MqttBrokerServer(sessionStoreService, this, mqttBrokerHandler, mqttServerTemplateProcessor);
        return thisMqttServer;
    }

    public MqttServerCreator port(int port) {
        this.port = port;
        return this;
    }

    public MqttServerCreator authService(IAuthService authService) {
        this.authService = authService;
        return this;
    }

    public MqttServerCreator retainMsgTime(long retainMsgTime) {
        this.retainMsgTime = retainMsgTime;
        return this;
    }

    public MqttServerCreator messageIdService(IMessageIdService messageIdService) {
        this.messageIdService = messageIdService;
        return this;
    }

    public MqttServerCreator mqttListenMessage(IMqttListenMessage mqttListenMessage) {
        this.mqttListenMessage = mqttListenMessage;
        return this;
    }

    public MqttServerCreator mqttListenConnect(IMqttListenConnect mqttListenConnect) {
        this.mqttListenConnect = mqttListenConnect;
        return this;
    }

    public MqttServerCreator checkSubscribeValidator(ICheckSubscribeValidator checkSubscribeValidator) {
        this.checkSubscribeValidator = checkSubscribeValidator;
        return this;
    }

    public MqttServerCreator wsEnable(boolean wsEnable) {
        this.wsEnable = wsEnable;
        return this;
    }

    public MqttServerCreator sslAuth(boolean sslAuth) {
        this.sslAuth = sslAuth;
        return this;
    }

    public MqttServerCreator cacheType(String cacheType) {
        this.cacheType = cacheType;
        return this;
    }

    public MqttServerCreator redisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        return this;
    }

    public MqttServerCreator maxTransMessage(int maxTransMessage) {
        this.maxTransMessage = maxTransMessage;
        return this;
    }

    @Builder
    @Data
    public static class SslConfig {
        //当 sslAuth =  true
        private boolean twoWay;
        /**
         * SSL端口号, 默认 18883
         */
        private int sslPort;
        /**
         * 启用ssl服务端认证
         */
        private boolean sslUserAuth;
        private String keystorePath;
        private String keystorePwd;
        //下面是双向认证，当 twoWay=true时 生效。
        private String twoWayCerChainFile;
        private String twoWayKeyFile;
        private String twoWayRootFile;
    }

    public SslConfig builderSslConfig(boolean twoWay, int sslPort, boolean sslUserAuth, String keystorePath, String keystorePwd,
                                      String twoWayCerChainFile, String twoWayKeyFile,String twoWayRootFile) {
        return new SslConfig(twoWay, sslPort, sslUserAuth, keystorePath, keystorePwd, twoWayCerChainFile, twoWayKeyFile,twoWayRootFile);
    }

    public MqttServerCreator sslConfig(SslConfig sslConfig) {
        this.sslConfig = sslConfig;
        return this;
    }
}
