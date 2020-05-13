package com.lagou.minicat.enums;

/**
 * http响应code码
 *
 * @author wlz
 * @date 2020/5/12
 */
public enum HttpResponseCode {
    /**
     * 请求成功
     */
    HTTP_OK(200, "OK"),

    /**
     * 请求资源不存在
     */
    HTTP_NOT_FOUND(404, "not found"),

    ;

    private int code;
    private String display;

    HttpResponseCode(int code, String display) {
        this.code = code;
        this.display = display;
    }

    public int getCode() {
        return code;
    }

    public String getDisplay() {
        return display;
    }
}
