package com.lagou.minicat.common.entity;

import com.lagou.minicat.enums.ServerStrategy;
import lombok.Data;

/**
 * minicat启动配置
 *
 * @author wlz
 * @date 2020/4/18
 */
@Data
public class MinicatBootstrapConfig {
    /**
     * 启动端口
     */
    private int port = 8080;
    /**
     * 最大工作线程数
     */
    private int maxThread = 100;
    /**
     * 服务使用策略
     */
    private ServerStrategy serverStrategy = ServerStrategy.HTTP_BIO;

    private String webAppsBasePackage = "E:\\学习经验\\mode2-task1-tomcat\\webapps";


}
