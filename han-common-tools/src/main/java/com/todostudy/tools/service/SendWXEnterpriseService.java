package com.todostudy.tools.service;

import com.todostudy.tools.fm.PC;
import com.todostudy.tools.utils.JacksonUtil;
import com.todostudy.tools.web.req.WXENDto;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author hanson
 * Date 2019-3-29
 * How to use? 企业微信消息发送
 * WXENDto dto = new WXENDto();
 * String con="发送内容来了";
 * List<String> list = new ArrayList<>();
 * //list.add();
 * dto.setText(dto.buildWXText(con,list));
 * <p>
 * SendWXEnterpriseService.builder().api("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=58647923-2bac-4f5c-aecd-a5b16f2feb47")
 * .build().sendMsg(dto);
 */
@Builder
@Slf4j
public class SendWXEnterpriseService {

    private String api;

    private static RestTemplate restTemplate = new RestTemplate();

    public String sendMsg(WXENDto json) {
        log.debug("==> data:{}", JacksonUtil.toString(json));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE));
        HttpEntity<String> httpEntity = new HttpEntity<String>(JacksonUtil.toString(json), headers);
        String forObject = restTemplate.postForObject(api, httpEntity, String.class);
        return forObject;
    }

}
