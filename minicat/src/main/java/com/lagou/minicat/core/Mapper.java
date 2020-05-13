package com.lagou.minicat.core;

/**
 * 映射接口
 *
 * @author wlz
 * @date 2020/4/18
 */
public interface Mapper {

    /**
     * 加载映射关系
     *
     * @param webAppPackage web应用基础路径
     */
    void load(String webAppPackage);

    /**
     * 通过url映射获取到对应的容器
     *
     * @param url 请求url
     * @return 返回对应的容器
     */
    Context mapper(String url);
}
