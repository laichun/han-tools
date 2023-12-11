# mqtt-broker
MQTT服务器功能的核心实现
认证部分，目前只有单向认证。
服务端使用 keystore/sChat.jks 时
客户端使用 sChat.cer;用mqtt.fx 连接 选择ca certificate file

第一步:   生成Netty服务端私钥和证书仓库命令，用于将客户端的证书保存到服务端的授信证书仓库中
keytool -genkey -alias securechat -keysize 2048 -validity 365 -keyalg RSA -dname "CN=localhost" -keypass 123456 -storepass 123456 -keystore sChat.jks

第二步：生成Netty服务端自签名证书 用于颁给使用者 从 证书仓库中导出证书
keytool -export -alias securechat -keystore sChat.jks -storepass sNetty -file sChat.cer

第三步：生成客户端的私钥和证书仓库，用于将服务端的证书保存到客户端的授信证书仓库中
    keytool -genkey -alias hanson -keysize 2048 -validity 365  -keyalg RSA -dname "CN=localhost" -keypass 123456  -storepass 123456 -keystore cChat.jks
第四步:生成客户端自签名证书

keytool -export -alias hanson -keystore cChat.jks -storepass 123456 -file cChat.cer

第五步：将Netty服务端证书导入到客户端的证书仓库中
keytool -import -trustcacerts -alias securechat -file sChat.cer -storepass 123456 -keystore cChat.jks
第六步:将客户端的自签名证书导入到服务端的信任证书仓库中：
keytool -import -trustcacerts -alias hanson -file cChat.cer -storepass 123456 -keystore sChat.jks

如果使用  client.keystore.jks
客户端使用 

## 关于双向认证
* 客户端需要3个证书。 对应 文件夹 ssl-server 和 ssl-client
* 生成方式，参考 https://www.alibabacloud.com/help/zh/iot/use-cases/connect-an-mqtt-gateway-device-to-iot-platform-by-using-mqtt-fx#9f1d9fa0767zf
* 阿里的文档有很细的说明。
## 区别
与单向认证不同的是， 双向认证中，服务端也需要对客户端进行安全认证，这就意味着客户端的自签名证书也需要导入到服务器的数组证书仓库中。
我们一般使用https，都是单向认证，就是我们详细该网站就是可信任的网站，不是伪造假冒的。
我们使用网上银行或者一些需要高安全性的服务时需要双向认证，因为有U盾之类的东西，银行或者其他需要高安全性的服务已经将颁发给我们的证书添加到自己的信任列表中了。
