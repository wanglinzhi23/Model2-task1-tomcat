package com.lagou.servletx;

/**
 * myServlet自定义异常
 *
 * @author wlz
 * @date 2020/5/12
 */
public class MyServletxException extends RuntimeException {
    public MyServletxException(String message) {
        super(message);
    }
}
