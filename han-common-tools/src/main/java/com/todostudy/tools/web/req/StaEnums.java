package com.todostudy.tools.web.req;

import lombok.Getter;

/**
 * laich  Date:2021/9/9
 *
 * @Desciprtion: 公共值
 */
public enum StaEnums {

    SUCCESS(1, "success"),
    FAIL(-1, "fail");


    @Getter
    private int code;
    @Getter
    private String msg;

    StaEnums(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
