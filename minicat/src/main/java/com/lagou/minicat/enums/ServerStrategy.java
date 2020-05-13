package com.lagou.minicat.enums;

/**
 * 服务策略，目前只提供httpBio
 *
 * @author wlz
 * @date 2020/5/12
 */
public enum ServerStrategy {

    /**
     * 服务将采用bio模型在传输层的io处理，使用http协议解析和处理数据
     */
    HTTP_BIO,

    ;
}
