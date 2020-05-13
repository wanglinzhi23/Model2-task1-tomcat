package com.lagou.minicat.core;

import com.lagou.minicat.common.entity.AcceptData;

/**
 * 传输层接口
 *
 * @author wlz
 * @date 2020/5/12
 */
public interface EndPoint {

    /**
     * 初始化工作
     */
    void init();

    /**
     * 监听并等到接收数据
     *
     * @return 返回接收到的数据
     */
    AcceptData accept();

}
