package com.lagou.servletx;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求方法类型枚举
 *
 * @author wlz
 * @date 2020/5/12
 */
public enum RequestMethodTypeEnum {

    /**
     * get请求
     */
    GET("GET", "get请求"),

    /**
     * post请求
     */
    POST("POST", "post请求"),

    ;

    private String name;
    private String desc;

    private static final Map<String, RequestMethodTypeEnum> TYPE_MAP;

    static {
        TYPE_MAP = new HashMap<>(values().length);
        for (RequestMethodTypeEnum value : values()) {
            TYPE_MAP.put(value.name, value);
        }
    }

    RequestMethodTypeEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public static RequestMethodTypeEnum getByName(String name) {
        return TYPE_MAP.get(name);
    }
}
