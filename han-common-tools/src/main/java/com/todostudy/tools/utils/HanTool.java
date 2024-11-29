package com.todostudy.tools.utils;

import com.todostudy.tools.fm.PC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HanTool {

    public static String UUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * @param str
     * @Description 将驼峰转为下划线
     */
    public static String xX2x_x(String str) {
        Pattern compile = Pattern.compile("[A-Z]");
        Matcher matcher = compile.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * @param str
     * @Description 将下划线转为驼峰
     */
    public static String x_x2xX(String str) {
        str = str.toLowerCase();
        Pattern compile = Pattern.compile("_[a-z]");
        Matcher matcher = compile.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(0).toUpperCase().replace("_", ""));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String replaceAll(String sourceStr, String reg, String replaceStr) {
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(sourceStr);
        return matcher.replaceAll(replaceStr);
    }

    /**
     * @param url
     * @param par     參數
     * @param method
     * @param headers header參數
     * @return String s1 = simpleHttp("http://xxx", null, HttpMethod.GET,headers);
     * @throws IOException
     * @throws InterruptedException
     */
    public static String simpleHttp(String url, Map par, HttpMethod method, Map<String, String> headers) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();//Java 11 HttpClient
        HttpRequest.Builder builder = null;
        if (method == HttpMethod.GET) {
            builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .GET();

        } else {
            HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(str_json_url(Optional.ofNullable(par).orElse(new HashMap()))));
        }

        if (headers != null && !CollectionUtils.isEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                String key = entry.getKey();
                if (builder != null) {
                    builder.setHeader(key, entry.getValue());
                }
            }
        }
        HttpRequest request = builder.build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static String simpleHttpJson(String url, Map par, Map<String, String> headers) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();//Java 11 HttpClient
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(JacksonUtil.toString(Optional.ofNullable(par).orElse(new HashMap()))));

        if (headers != null && !CollectionUtils.isEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                String key = entry.getKey();
                builder.setHeader(key, entry.getValue());
            }
        }

        HttpRequest request = builder.build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static String post(String url, String data) {
        HttpClient httpClient = HttpClient.newHttpClient();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .header("Content-Type", "application/json; charset=utf-8")
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(data, StandardCharsets.UTF_8))
                    //.timeout(BK.t1000)
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == PC.OK_CODE) {
                return response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String postAsync(String url, String data) {
        HttpClient httpClient = HttpClient.newHttpClient();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .header("Content-Type", "application/json; charset=utf-8")
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(data, StandardCharsets.UTF_8))
                    //.timeout(BK.t1000)
                    .build();
            CompletableFuture<HttpResponse<String>> responseCompletableFuture = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = responseCompletableFuture.get();
            if (response.statusCode() == PC.OK_CODE) {
                return response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param hashMap
     * @return key1=value1&key2=value2
     */
    public static String str_json_url(Map<String, Object> hashMap) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : hashMap.entrySet()) {
            if (builder.length() > PC.t0) {
                builder.append(PC.f_url);
            }
            builder.append(entry.getKey()).append(PC.f_eq).append(entry.getValue());
        }
        return builder.toString();
    }

    /**
     * @param version1
     * @param version2
     * @return if the value is -1 ,means version2 is large。 If the value is 1,mean version1 is large.
     *  and the value is 0 ,version2 EQ version1
     */
    public static int compareVersion(String version1, String version2) {
        // 解析两个版本号
        int[] v1 = parseVersion(version1);
        int[] v2 = parseVersion(version2);
        // 取两个版本号中较长的数组长度
        int length = Math.max(v1.length, v2.length);

        for (int i = PC.t0; i < length; i++) {
            // 获取当前比较部分的值，如果越界则视为0
            int num1 = (i < v1.length) ? v1[i] : PC.t0;
            int num2 = (i < v2.length) ? v2[i] : PC.t0;

            if (num1 < num2) {
                return -1; // version1 小于 version2
            } else if (num1 > num2) {
                return PC.t1; // version1 大于 version2
            }
        }
        return PC.t0; // 两个版本号相等
    }

    public static int[] parseVersion(String version) {
        //转小写 去掉V
        version=version.toLowerCase().replace("v","");

        // 用点分割版本号，将其转换为字符串数组
        String[] parts = version.split("\\.");
        // 创建一个整数数组，以存储解析后的版本号
        int[] versionNumbers = new int[parts.length];
        // 将字符串数组转换成整数数组
        for (int i = PC.t0; i < parts.length; i++) {
            versionNumbers[i] = Integer.parseInt(parts[i]);
        }
        return versionNumbers; // 返回整数数组
    }

    private static HanTool snowFlakeUtil;
    static {
        snowFlakeUtil = new HanTool();
    }

    // 初始时间戳(纪年)，可用雪花算法服务上线时间戳的值
    // 1650789964886：2022-04-24 16:45:59
    private static final long INIT_EPOCH = 1650789964886L;

    // 时间位取&
    private static final long TIME_BIT = 0b1111111111111111111111111111111111111111110000000000000000000000L;

    // 记录最后使用的毫秒时间戳，主要用于判断是否同一毫秒，以及用于服务器时钟回拨判断
    private long lastTimeMillis = -1L;

    // dataCenterId占用的位数
    private static final long DATA_CENTER_ID_BITS = 5L;

    // dataCenterId占用5个比特位，最大值31
    // 0000000000000000000000000000000000000000000000000000000000011111
    private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);

    // dataCenterId
    private long dataCenterId;

    // workId占用的位数
    private static final long WORKER_ID_BITS = 5L;

    // workId占用5个比特位，最大值31
    // 0000000000000000000000000000000000000000000000000000000000011111
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

    // workId
    private long workerId;

    // 最后12位，代表每毫秒内可产生最大序列号，即 2^12 - 1 = 4095
    private static final long SEQUENCE_BITS = 12L;

    // 掩码（最低12位为1，高位都为0），主要用于与自增后的序列号进行位与，如果值为0，则代表自增后的序列号超过了4095
    // 0000000000000000000000000000000000000000000000000000111111111111
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    // 同一毫秒内的最新序号，最大值可为 2^12 - 1 = 4095
    private long sequence;

    // workId位需要左移的位数 12
    private static final long WORK_ID_SHIFT = SEQUENCE_BITS;

    // dataCenterId位需要左移的位数 12+5
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    // 时间戳需要左移的位数 12+5+5
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    /**
     * 无参构造
     */
    public HanTool() {
        this(1, 1);
    }

    /**
     * 有参构造
     * @param dataCenterId
     * @param workerId
     */
    public HanTool(long dataCenterId, long workerId) {
        // 检查dataCenterId的合法值
        if (dataCenterId < 0 || dataCenterId > MAX_DATA_CENTER_ID) {
            throw new IllegalArgumentException(
                    String.format("dataCenterId 值必须大于 0 并且小于 %d", MAX_DATA_CENTER_ID));
        }
        // 检查workId的合法值
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException(String.format("workId 值必须大于 0 并且小于 %d", MAX_WORKER_ID));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    /**
     * 获取唯一ID
     * @return
     */
    public static Long getSnowFlakeId() {
        return snowFlakeUtil.nextId();
    }

    /**
     * 通过雪花算法生成下一个id，注意这里使用synchronized同步
     * @return 唯一id
     */
    public synchronized long nextId() {
        long currentTimeMillis = System.currentTimeMillis();
        // 当前时间小于上一次生成id使用的时间，可能出现服务器时钟回拨问题
        if (currentTimeMillis < lastTimeMillis) {
            throw new RuntimeException(
                    String.format("可能出现服务器时钟回拨问题，请检查服务器时间。当前服务器时间戳：%d，上一次使用时间戳：%d", currentTimeMillis,
                            lastTimeMillis));
        }
        if (currentTimeMillis == lastTimeMillis) {
            // 还是在同一毫秒内，则将序列号递增1，序列号最大值为4095
            // 序列号的最大值是4095，使用掩码（最低12位为1，高位都为0）进行位与运行后如果值为0，则自增后的序列号超过了4095
            // 那么就使用新的时间戳
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                currentTimeMillis = getNextMillis(lastTimeMillis);
            }
        } else { // 不在同一毫秒内，则序列号重新从0开始，序列号最大值为4095
            sequence = 0;
        }
        // 记录最后一次使用的毫秒时间戳
        lastTimeMillis = currentTimeMillis;
        // 核心算法，将不同部分的数值移动到指定的位置，然后进行或运行
        // <<：左移运算符, 1 << 2 即将二进制的 1 扩大 2^2 倍
        // |：位或运算符, 是把某两个数中, 只要其中一个的某一位为1, 则结果的该位就为1
        // 优先级：<< > |
        return
                // 时间戳部分
                ((currentTimeMillis - INIT_EPOCH) << TIMESTAMP_SHIFT)
                        // 数据中心部分
                        | (dataCenterId << DATA_CENTER_ID_SHIFT)
                        // 机器表示部分
                        | (workerId << WORK_ID_SHIFT)
                        // 序列号部分
                        | sequence;
    }

    /**
     * 获取指定时间戳的接下来的时间戳，也可以说是下一毫秒
     * @param lastTimeMillis 指定毫秒时间戳
     * @return 时间戳
     */
    private long getNextMillis(long lastTimeMillis) {
        long currentTimeMillis = System.currentTimeMillis();
        while (currentTimeMillis <= lastTimeMillis) {
            currentTimeMillis = System.currentTimeMillis();
        }
        return currentTimeMillis;
    }

    /**
     * 获取随机字符串,length=13
     * @return
     */
    public static String getRandomStr() {
        return Long.toString(getSnowFlakeId(), Character.MAX_RADIX);
    }

    /**
     * 从ID中获取时间
     * @param id 由此类生成的ID
     * @return
     */
    public static Date getTimeBySnowFlakeId(long id) {
        return new Date(((TIME_BIT & id) >> 22) + INIT_EPOCH);
    }



}
