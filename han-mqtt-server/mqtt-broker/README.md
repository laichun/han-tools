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

