package com.lagou.servletx;

/**
 * servlet对象
 *
 * @author wlz
 * @date 2020/5/12
 */
public interface MyServlet {
    /**
     * 请求处理
     *
     * @param request  请求对象
     * @param response 响应对象
     */
    default void doService(MyRequest request, MyResponse response) {
        RequestMethodTypeEnum methodType = request.getMethodType();
        switch (methodType) {
            case GET:
                doGet(request, response);
                break;
            case POST:
                doPost(request, response);
                break;
            default:
                throw new MyServletxException("methodType not supported");
        }
    }

    /**
     * get请求处理
     *
     * @param request  请求对象
     * @param response 响应对象
     */
    void doGet(MyRequest request, MyResponse response);

    /**
     * post请求处理
     *
     * @param request  请求对象
     * @param response 响应对象
     */
    void doPost(MyRequest request, MyResponse response);
}
