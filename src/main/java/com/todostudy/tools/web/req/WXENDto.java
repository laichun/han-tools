package com.todostudy.tools.web.req;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author  hanson
 * 微信api数据
 */
@Data
public class WXENDto implements Serializable {

    private String msgtype="text";

    private WxText text;

    public WxText buildWXText(String con,List<String> list){
        return WxText.builder().content(con).mentioned_list(list).build();
    }

}

@Data
@Builder
class WxText{
    private String content;

    private List<String> mentioned_list;
}