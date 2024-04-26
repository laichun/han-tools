# iot-mqtt-server
本项目 fork from https://gitee.com/recallcode/iot-mqtt-server
感谢作者开源！本人在项目中使用，改了部分代码。

#### 项目介绍
轻量级物联网MQTT服务器, 快速部署, 支持集群.

#### 软件架构说明
基于netty+springboot+redis技术栈实现
1. 使用netty实现通信及协议解析
2. 使用springboot提供依赖注入及属性配置
3. 使用memory 或者 redis 实现存储, 分布式锁, 集群和集群间通信
4. 支持集群，支持ssl 单向和双向认证。


#### 项目结构
```
iot-mqtt-server
  ├── mqtt-example -- MQTT服务连接案例
  ├── mqtt-broker -- MQTT broker 服务器
  ├── mqtt-common -- 公共类及其他模块使用的服务接口及对象
  ├── mqtt-server --  MQTT服务器功能的核心实现
  ├── han-mqtt-service-spring-stater spring 集成stater
```

#### 功能说明
1. 参考MQTT3.1.1规范实现
2. 完整的QoS服务质量等级实现
3. 遗嘱消息, 保留消息及消息分发重试
4. 心跳机制
5. 连接认证(强制开启)
6. 支持 第三语言 http 对接.
7. SSL方式连接(支持非SSL连接)
8. 主题过滤(未完全实现标准: 以#或+符号开头的、以/符号结尾的及不存在/符号的订阅按非法订阅处理, 这里没有参考标准协议)
9. websocket支持
10.  集群功能

#### 快速开始
参考 example
```
    <dependency>
			<groupId>com.todostudy</groupId>
			<artifactId>han-mqtt-service-spring-stater</artifactId>
			<version>版本号 version</version>
		</dependency>
```
* 认证代码
```
@Service
public class AuthService implements IAuthService {

	public boolean verifyAuth(String username, String password,String clientId) {
		//实现业务
			return true;

	}
```
* 上线下线通知
```
@Slf4j
@Service
public class MqttListenConnectServer implements IMqttListenConnect {

    @Autowired
    private IMqttServerTemplate mqttServerTemplate;

    @Override
    public void online(String clientId, String username) {
        log.info("--online--{}", clientId);
    }

    @Override
    public void offline(String clientId, String username, String reason) {
        log.info("------offline--{}", clientId);
    }
}
```
* 消息监听
```
@Slf4j
@Service
public class ListenMessageServer implements IMqttListenMessage {

    @Autowired
    private IMqttServerTemplate mqttServerTemplate;

    @Override
    public void onMessage(String clientId, String topic, MqttQoS qos, byte[] message){
        //业务处理...
        log.info("--onMessage---clientId:{},msg:{}",clientId, new String(message));
    }
}
```
```
增加对websocket url 传参的支持(例如：/aaa/bbb?token=abababab&userName=hanson)，配置文件  ws-model = 2 ; 心跳包 ws.send('Heartbeat Packet'); 默认60秒。
ws-model = 1 ;和mqtt方式一样。通过topic 订阅 。
```
#### 集群使用
1. 开启redis 。主要存储 topic 
2. 前端加代理 IP 负载即可以。


#### 自定义 - 连接认证
- 默认只是简单使用对用户名进行RSA密钥对加密生成密码, 连接认证时对密码进行解密和相应用户名进行匹配认证
- 使用中如果需要实现连接数据库或其他方式进行连接认证, 只需要重写`mqtt-auth`模块下的相应方法即可

#### 自定义 - 服务端证书
- 服务端证书存储在`mqtt-broker`的`resources/keystore/`
- 用户可以制作自己的证书, 但存储位置和文件名必须使用上述描述的位置及文件名

#### 生成环境部署

#### 测试和补充

qos0
对于qos1而言，对于client而言，有且仅发一次publish包，对于broker而言，有且仅发一次publish，简而言之，就是仅发一次包，是否收到完全不管，适合那些不是很重要的数据。
QoS 0 是最低的 QoS等级。QoS 0 消息即发即弃，不需要等待确认，不需要存储和重传，因此对于接收方来说，永远都不需要担心收到重复的消息。


QoS=1通讯时的注意事项
qos1
对于qos0而言，这个交互就是多了一次ack的作用，但是会有个问题，尽管我们可以通过确认来保证一定收到客户端或服务器的message，
但是我们却不能保证message仅有一次，也就是当client没收到service的puback或者service没有收到client的puback，那么就会一直发送publisher
qos1流程：（publisher -> broker）
1.publisher store msg -> publish ->broker （传递message）
2.broker -> puback -> publisher delete msg （确认传递成功）

测试注意：
如想在MQTT通讯中实现服务质量等级为1级（QoS=1），我们要分别对消息的发布端课接收端进行相应的设置。以下列表中的内容是具体需要采取的措施。
接收端连接服务端时cleanSession设置为false
接收端订阅主题时QoS=1
发布端发布消息时QoS=1



QoS=2通讯时的注意事项
如想在MQTT通讯中实现服务质量等级为2级（QoS=2），我们要分别对消息的发布端和接收端进行相应的设置。以下列表中的内容是具体需要采取的措施。

qos2流程：（publisher -> broker）
publisher store msg -> publish ->broker -> broker store msgID（传递message）
broker -> puberc （确认传递成功）
publisher -> pubrel -> broker delete msgID （告诉broker删除msgID）
broker -> pubcomp -> publisher delete msg （告诉publisher删除msg）

测试注意：
接收端连接服务端时cleanSession设置为false
接收端订阅主题时QoS=2
发布端发布消息时QoS=2

QoS=2 会比较慢。QoS 用 0或者1 适用大多数场景。

Retain 消息 自行百度


