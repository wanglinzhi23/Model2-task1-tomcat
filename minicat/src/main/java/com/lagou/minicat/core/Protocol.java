package com.lagou.minicat.core;

import com.lagou.minicat.common.entity.AcceptData;
import com.lagou.minicat.common.entity.ProtocolData;

/**
 * 协议处理
 *
 * @author wlz
 * @date 2020/5/12
 */
public interface Protocol {
    /**
     * 协议处理
     *
     * @param acceptData 传输层接收的数据
     * @return 返回协议数据
     */
    ProtocolData protocol(AcceptData acceptData);
}
