package com.todostudy.tools.service;

import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;

/**
 * 初始化MQTT客户端
 *
 * @author hanson
 */
@Slf4j
@Data
public class MqttPahoService {
    /**
     * MQTT客户端
     */
    private static MqttClient client = null;
    /**
     * 连接选项
     */
    private static MqttConnectOptions connOpts = null;
    /**
     * 连接状态
     */
    private static Boolean connectStatus = false;
    private MqttPahoService(){}
    public MqttPahoService(String clientId,String userName, String passWord, String address) {
        this.address = address;
        this.userName = userName;
        this.passWord = passWord;
        this.clientId = clientId;
    }
    public MqttPahoService setMsgCallBack(MqReceiveInterface mqReceiveInterface){
        this.mqReceiveInterface = mqReceiveInterface;
        return this;
    }
    @Setter
    private String userName;
    @Setter
    private String passWord;
    @Setter
    private String address;
    @Setter
    private String clientId;

    private MqReceiveInterface mqReceiveInterface;


    /**
     * 获取MQTT客户端连接状态
     *
     * @return
     */
    public static Boolean getConnectStatus() {
        return connectStatus;
    }

    /**
     * MQTT客户端启动
     */
//    @PostConstruct
    public void connect() {
        if (userName == null || address == null || passWord == null) {
            return;
        }
        try {
            // MQTT 连接选项
            connOpts = new MqttConnectOptions();
            // 设置认证信息
            connOpts.setUserName(userName);
            connOpts.setPassword(passWord.toCharArray());
            connOpts.setConnectionTimeout(10);
            connOpts.setKeepAliveInterval(90);
            //setting，clear session, The client restart will not receive mq message.
            //  持久化
            //  MemoryPersistence persistence = new MemoryPersistence();
            // MQ客户端建立
            client = new MqttClient(address, clientId);
            // 设置回调
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    log.error("connection lost：{}", cause.getMessage());
                    reconnection();
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // Processing received messages does not require processing.
                    if(mqReceiveInterface!=null) {
                        mqReceiveInterface.dealMessageArrived(topic, message);
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Processing the completion of message sending ,does not require processing.
                }
            });
            // Establish connection
            client.connect(connOpts);
            connectStatus = client.isConnected();
            log.info("MQTT服务器连接成功~~~ {}", address);
        } catch (Exception e) {
            connectStatus = client.isConnected();
            log.error("MQTT服务器连接失败!!", e);
            reconnection();
        }
    }

    /**
     * 消息订阅
     *
     * @param topic 主题
     * @param qos   QOS
     */
    public void subscribe(String topic, Integer qos) throws MqttException {
        client.subscribe(topic, qos);
    }

    /**
     * 消息发布
     *
     * @param topic   主题
     * @param message 消息体
     * @param qos     QOS
     */
    public void publish(String topic, String message, Integer qos) throws MqttException {
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        mqttMessage.setQos(qos);
        client.publish(topic, mqttMessage);
    }

    /**
     * 断线重连
     */
    public void reconnection() {
        // 尝试进行重新连接
        while (true) {
            if (getConnectStatus()) {
                // 查询连接状态 连接成功则停止重连
                break;
            }
            try {
                log.info("开始进行MQTT服务器连接.......");
                connect();
                Thread.sleep(15 * 1000);
            } catch (Exception e) {
                log.error("重新连接出现异常 : time out");
                //e.printStackTrace();
                break;
            }
        }
    }

}
