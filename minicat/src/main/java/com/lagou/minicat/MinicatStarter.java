package com.lagou.minicat;

import com.lagou.minicat.common.entity.MinicatBootstrapConfig;
import com.lagou.minicat.core.MinicatBootStrap;

/**
 * minicat启动类
 *
 * @author wlz
 * @date 2020/4/18
 */
public class MinicatStarter {
    public static void main(String[] args) {
        MinicatBootstrapConfig config = new MinicatBootstrapConfig();
        config.setPort(9090);
        // 创建启动类
        MinicatBootStrap minicatBootStrap = new MinicatBootStrap(config);
        // 初始化
        minicatBootStrap.init();
        // 启动minicat
        minicatBootStrap.start();
    }
}
