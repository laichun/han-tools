package com.todostudy.iot.mqtt.server.common;

import cn.hutool.core.lang.Singleton;

import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 需要使用jdk11 ，只有这里使用了JDK11的特性。
 * @author  hanson
 */
public class Tools {

    private static Tools toolsInstance;//单例

    public static final String clientId="clientId";
    public static final String username="username";
    public static final String topic="topic";
    public static final String CACHE_MEMORY="memory";
    public static final String CACHE_REDIS="redis";
    private final Duration timeout = Duration.ofSeconds(10);

    private final byte[] lock = new byte[0];
    private volatile HttpClient httpClient = null;
    private static ExecutorService godoService;

    public Tools httpPostBuilder(){
        if (httpClient == null){
            synchronized (lock){
                if (httpClient == null){
                    httpClient = HttpClient.newBuilder()
                            .version(HttpClient.Version.HTTP_2)
                            .connectTimeout(timeout)
                            .followRedirects(HttpClient.Redirect.NEVER)
                            //.sslContext(sslContext())
                            .proxy(ProxySelector.getDefault())
                            .build();
                }
            }
        }
        return this;
    }

    /**
     * get请求
     * @param url 地址
     * @return
     */
    public String postJson(String url,String data){
        String body = "";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .header("Content-Type", "application/json")
                    .version(HttpClient.Version.HTTP_2)
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(data, Charset.defaultCharset()))
                    .timeout(timeout)
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200){
                body = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }

    public static Tools getInstance() {
        if (toolsInstance == null) {
            toolsInstance = new Tools();
        }
        return toolsInstance;
    }
    /**
     * 创建 HttpUtil
     * @return
     */
    public static Tools httpBuilder(){
        return getInstance().httpPostBuilder();
    }

    public static ExecutorService getGodo(){
        if(godoService==null){
            godoService = Executors.newSingleThreadExecutor();
        }
        return godoService;
    }

}
