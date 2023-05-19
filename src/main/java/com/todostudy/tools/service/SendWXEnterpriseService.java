package com.todostudy.tools.service;

import com.todostudy.tools.utils.JacksonUtil;
import com.todostudy.tools.web.req.WXENDto;
import lombok.Builder;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

/**
 * @author hanson
 * Date 2019-3-29
 * How to use? 企业微信消息发送
 *   WXENDto dto = new WXENDto();
 *         String con="发送内容来了";
 *         List<String> list = new ArrayList<>();
 *         //list.add();
 *         dto.setText(dto.buildWXText(con,list));
 *
 *         SendWXEnterpriseService.builder().api("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=58647923-2bac-4f5c-aecd-a5b16f2feb47")
 *                 .build().sendMsg(dto);
 */
@Builder
public class SendWXEnterpriseService {

    private String api;

    private static RestTemplate restTemplate =new RestTemplate();

    public String sendMsg(WXENDto json){
        System.out.println(JacksonUtil.toString(json));
        HttpEntity<String> httpEntity = new HttpEntity<String>(JacksonUtil.toString(json));
        String forObject = restTemplate.postForObject(api, httpEntity,String.class);
        return forObject;
    }

}
