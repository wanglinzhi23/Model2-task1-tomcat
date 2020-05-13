package com.lagou.minicat.core;

import com.lagou.minicat.common.entity.ProtocolData;

/**
 * web程序容器
 *
 * @author wlz
 * @date 2020/4/18
 */
public interface Context {
    /**
     * 请求协议数据交给容器内部映射处理
     *
     * @param protocolData 协议数据
     */
    void doContextMapper(ProtocolData protocolData);
}
