# mqtt-broker
MQTT服务器功能的核心实现
keytool 生成
keytool生成和openssl有些不一样
```
第一步 生成服务器端私钥和证书仓库命令
keytool -genkey -alias hansonServer -keysize 2048 -validity 365 -keyalg RSA -dname "CN=localhost" -keypass 123456 -storepass 1234qwer -keystore yqServer.jks
```
> 注意jdk11 不用指定 -keypass 123456
-keysize 2048 密钥长度2048位（这个长度的密钥目前可认为无法被暴力破解）
-validity 365 证书有效期365天，测试中365就高了，实际生产中我们会冲认证机构获取证书，有效期比较长
-keyalg RSA 使用RSA非对称加密算法
-dname "CN=localhost" 设置Common Name为localhost
-keypass skeypass123 密钥的访问密码为skeypass123
-storepass sstorepass456 密钥库的访问密码为sstorepass456
-keystore sChat.jks 指定生成的密钥库文件为sChata.jks
> 
```
第二步 生成服务器端自签名证书
keytool -export -alias hansonServer -keystore yqServer.jks -storepass 1234qwer -file yqServer.cer
```
```
第三步 ：生成客户端的密钥对和证书仓库，用于将服务器端的证书保存到客户端的授信证书仓库中
keytool -genkey -alias hanson -keysize 2048 -validity 365 -keyalg RSA -dname "CN=localhost" -keypass 123456 -storepass 1234qwer -keystore yqClient.jks
>> 注意jdk11 不用指定 -keypass 123456 用下面的指令
keytool -genkey -alias hanson -keysize 2048 -validity 365 -keyalg RSA -dname "CN=localhost" -storepass 1234qwer -keystore yqClient.jks
```
```
第四步 ：将服务器端证书导入到客户端的证书仓库中
keytool -import -trustcacerts -alias hansonServer -file yqServer.cer -storepass 1234qwer -keystore yqClient.jks
```
* 如果只做单向认证，到此就可以结束了，server端使用 yqServer.jks 和密码，客户端使用 client.jks如果是双响认证，则还需第五步和第六步.
```
第五步 生成客户端自签名证书
keytool -export -alias hanson -keystore yqClient.jks -storepass 1234qwer -file yqClient.cer
```
```
第六步 将客户端的自签名证书导入到服务器端的信任证书仓库中：
keytool -import -trustcacerts -alias hanson -file yqClient.cer -storepass 1234qwer -keystore yqServer.jks
```
到此，证书就生成完毕了，我们就可以得到两个jks文件，一个是服务端的yqServer.jks ，一个是客户端的yqClient.jks , 
两个cer文件yqServer.cer和yqClient.cer  共4个文件，服务端和客户端都需要2个证书。 这种方式和使用第三方 
openssl生成的双向认证有出入我们使用服务端和客户端3个证书那种，当然用 netty 对这2种方法的双向认证都是支持的。

* 目前的代码对双向认证的支持采用 openssl 的生成方式。 即服务端和客户端3个证书那种：
## 关于双向认证
* 客户端需要3个证书。 对应 文件夹 ssl-server 和 ssl-client
* 生成方式，参考 https://www.alibabacloud.com/help/zh/iot/use-cases/connect-an-mqtt-gateway-device-to-iot-platform-by-using-mqtt-fx#9f1d9fa0767zf
* 阿里的文档有很细的说明。
## 区别
与单向认证不同的是， 双向认证中，服务端也需要对客户端进行安全认证，这就意味着客户端的自签名证书也需要导入到服务器的数组证书仓库中。
我们一般使用https，都是单向认证，就是我们详细该网站就是可信任的网站，不是伪造假冒的。
我们使用网上银行或者一些需要高安全性的服务时需要双向认证，因为有U盾之类的东西，银行或者其他需要高安全性的服务已经将颁发给我们的证书添加到自己的信任列表中了。
