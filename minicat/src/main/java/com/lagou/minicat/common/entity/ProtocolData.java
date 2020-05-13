package com.lagou.minicat.common.entity;

import com.lagou.servletx.MyRequest;
import com.lagou.servletx.MyResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 协议数据
 *
 * @author wlz
 * @date 2020/5/12
 */
@Data
@AllArgsConstructor
public class ProtocolData {
    private MyRequest request;
    private MyResponse response;
    private boolean responsed;
}
