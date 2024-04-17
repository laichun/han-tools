
package com.todostudy.hans.stater.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 服务配置
 */
@Setter
@Getter
@ConfigurationProperties(prefix = HanMqttBrokerProperties.PREFIX)
public class HanMqttBrokerProperties {
	/**
	 * 配置前缀
	 */
	public static final String PREFIX="han.mqtt.broker";

	/**
	 * 是否启用，默认：启用
	 */
	private boolean enabled = true;

	/**
	 * 缓存类型
	 */
	private String cacheType;

	/**
	 * 默认不开启ssl认证
	 */
	private boolean sslAuth;

	/**
	 * 是否开启websocket
	 */
	private boolean wsEnable;

	/**
	 * Broker唯一标识
	 */
	private String id;

	/**
	 * 端口 默认 1883
	 */
	private int port=1883;

	private boolean wsEnableSsl = false;
	/**
	 * WebSocket SSL端口号, 默认9993端口
	 */
	private int websocketSslPort = 9995;

	/**
	 * WebSocket Path值, 默认值 /mqtt
	 */
	private String websocketPath = "/mqtt";
	/**
	 * 默认2m的传输大小。单位 M
	 */
	private int maxTransMessage=2;

	/**
	 * 心跳时间(秒), 默认60秒, 该值可被客户端连接时相应配置覆盖
	 */
	private int keepAlive = 60;

	/**
	 * 是否开启Epoll模式, 默认关闭
	 */
	private boolean useEpoll = false;

	/**
	 * Sokcet参数, 存放已完成三次握手请求的队列最大长度, 默认511长度
	 */
	private int soBacklog = 511;

	/**
	 * Socket参数, 是否开启心跳保活机制, 默认开启
	 */
	private boolean soKeepAlive = true;

	/**
	 * 集群配置, 是否基于组播发现, 默认开启
	 */
	private boolean enableMulticastGroup = true;

	/**
	 * 集群配置, 基于组播发现
	 */
	private String multicastGroup;

	/**
	 * 集群配置, 当组播模式禁用时, 使用静态IP开启配置集群
	 */
	private String staticIpAddresses;
	/**
	 * Retain 消息过期时间
	 */
	private long retainMsgTime=10; //小时

	private HttpApi httpApi = new HttpApi();
	private boolean httpEnable;

	private SslConfig sslConfig = new SslConfig();
	@Setter
	@Getter
	public static class SslConfig{
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

	@Setter
	@Getter
	public static class HttpApi{
		private String sendAuth;
		private String sendSubTopic;
		private String sendMsg;
		private String sendState;
	}

}
