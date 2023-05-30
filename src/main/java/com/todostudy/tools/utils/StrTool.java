package com.todostudy.tools.utils;

import com.todostudy.tools.fm.PC;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrTool {

    public static String UUID(){
        return UUID.randomUUID().toString().replace("-","");
    }

    /**
     * @Description 将驼峰转为下划线
     * @param str
     */
    public static String xX2x_x(String str) {
        Pattern compile = Pattern.compile("[A-Z]");
        Matcher matcher = compile.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()) {
            matcher.appendReplacement(sb,  "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * @Description 将下划线转为驼峰
     * @param str
     */
    public static String x_x2xX(String str) {
        str = str.toLowerCase();
        Pattern compile = Pattern.compile("_[a-z]");
        Matcher matcher = compile.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()) {
            matcher.appendReplacement(sb,  matcher.group(0).toUpperCase().replace("_",""));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String replaceAll(String sourceStr,String reg,String replaceStr) {
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(sourceStr);
        return matcher.replaceAll(replaceStr);
    }

    /**
     * @param url
     * @param par
     * @param method
     * @param headers
     * @return String s1 = simpleHttp("http://xxx", null, HttpMethod.GET,headers);
     * @throws IOException
     * @throws InterruptedException
     */
    public static String simpleHttp(String url, Map par, HttpMethod method,Map<String, String> headers) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();//Java 11 HttpClient
        HttpRequest.Builder builder= null;
        if(method==HttpMethod.GET) {
             builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .GET();

        } else if (method == HttpMethod.POST) {
              HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header(HttpHeaders.CONTENT_TYPE,"application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(str_json_url(Optional.ofNullable(par).orElse(new HashMap()))));

        }

        if(headers!=null && !CollectionUtils.isEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                String key = entry.getKey();
                builder.setHeader(key,entry.getValue());
            }
        }
        HttpRequest request=builder.build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
    public static String simpleHttpJson(String url, Map par,Map<String, String> headers) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();//Java 11 HttpClient
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(JacksonUtil.toString(Optional.ofNullable(par).orElse(new HashMap()))));

        if(headers!=null && !CollectionUtils.isEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                String key = entry.getKey();
                builder.setHeader(key,entry.getValue());
            }
        }

        HttpRequest request=builder.build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    /**
     * @param hashMap
     * @return key1=value1&key2=value2
     */
    public static String str_json_url(Map<String, Object> hashMap){
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : hashMap.entrySet()) {
            if (builder.length() > 0) {
                builder.append(PC.f_url);
            }
            builder.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return builder.toString();
    }

}
