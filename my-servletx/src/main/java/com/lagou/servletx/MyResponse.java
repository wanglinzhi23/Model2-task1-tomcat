package com.lagou.servletx;

import java.io.OutputStream;

/**
 * 响应对象
 *
 * @author wlz
 * @date 2020/5/12
 */
public interface MyResponse {

    /**
     * 写入数据
     *
     * @param data 数据
     */
    void write(String data);

    /**
     * 获取到输出流
     */
    OutputStream getOutputStream();

    /**
     * 刷新输出流
     */
    void flush();

    /**
     * 通知客户端服务无法响应该请求
     */
    void notFound();
}
