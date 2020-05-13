package com.lagou.servletx;

/**
 * 请求对象
 *
 * @author wlz
 * @date 2020/5/12
 */
public interface MyRequest {

    /**
     * 获取请求url
     */
    String getUrl();

    /**
     * 获取远程访问主机ip
     */
    String getRemoteHost();

    /**
     * 获取请求方法类型
     */
    RequestMethodTypeEnum getMethodType();

    /**
     * 获取参数
     *
     * @param paramName 参数名称
     * @return 返回参数值
     */
    String getParam(String paramName);

}
