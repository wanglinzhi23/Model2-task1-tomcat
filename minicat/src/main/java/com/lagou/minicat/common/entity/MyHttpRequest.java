package com.lagou.minicat.common.entity;

import com.lagou.servletx.MyRequest;
import com.lagou.servletx.RequestMethodTypeEnum;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * http请求
 *
 * @author wlz
 * @date 2020/5/12
 */
@Setter
public class MyHttpRequest implements MyRequest {
    private String url;
    private String remoteHost;
    private RequestMethodTypeEnum methodType;
    private Map<String, String> params;

    public MyHttpRequest() {
        params = new HashMap<>();
    }

    @Override
    public String getUrl() {

        return url;
    }

    @Override
    public String getRemoteHost() {

        return remoteHost;
    }

    @Override
    public RequestMethodTypeEnum getMethodType() {

        return methodType;
    }

    @Override
    public String getParam(String paramName) {

        return params.get(paramName);
    }

    public void addParam(String key, String value) {
        params.put(key, value);
    }


}
