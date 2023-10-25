package com.todostudy.tools.web.req;

import com.todostudy.tools.fm.PC;
import lombok.Data;

/**
 * author:laich  Date:2021/9/9
 *
 * @Desciprtion:
 */
@Data
public class RData {

    private int code;

    private String message;

    private Object data;

    private RData() {
    }

    private RData(int code) {
        this(code, null);
    }

    private RData(int code, String message) {
        this(code, message, null);
    }

    private RData(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static RData fail() {
        return fail(PC.ERROR_CODE, StaEnums.FAIL.name(), null);
    }

    public static RData fail(String message) {
        return fail(PC.ERROR_CODE, message, null);
    }

    public static RData fail(int code, String message) {
        return fail(code, message, null);
    }

    public static RData fail(String message, Object data) {
        return fail(PC.ERROR_CODE, message, data);
    }

    public static RData fail(int code, String message, Object data) {
        return new RData(code, message, data);
    }

    public static RData success() {
        return success(PC.OK_CODE, StaEnums.SUCCESS.name(), null);
    }

    public static RData success(Object data) {
        return success(PC.OK_CODE, StaEnums.SUCCESS.name(), data);
    }

    public static RData success(String message) {
        return success(PC.OK_CODE, message, null);
    }

    public static RData success(String message, Object data) {
        return success(PC.OK_CODE, message, data);
    }

    public static RData success(int code, String message, Object data) {
        return new RData(code, message, data);
    }


}
