package com.lagou.minicat.core;

import com.lagou.minicat.core.endpoints.BioEndPoint;
import com.lagou.minicat.core.mappers.LocalStorageMapper;
import com.lagou.minicat.core.protocols.HttpProtocol;
import com.lagou.minicat.enums.ServerStrategy;
import com.lagou.minicat.common.MinicatThreadFactory;
import com.lagou.minicat.common.entity.AcceptData;
import com.lagou.minicat.common.entity.MinicatBootstrapConfig;
import com.lagou.minicat.common.entity.ProtocolData;
import com.lagou.minicat.exceptions.MinicatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * minicat启动器，也是入口类
 *
 * @author wlz
 * @date 2020/5/12
 */
public class MinicatBootStrap {
    private static final Logger logger = LoggerFactory.getLogger(MinicatBootStrap.class);
    private EndPoint endPoint;
    private Protocol protocol;
    private Mapper mapper;
    private ThreadPoolExecutor masterThreadPool;
    private ThreadPoolExecutor workerThreadPool;
    private volatile boolean stopMark;
    private MinicatBootstrapConfig config;

    public MinicatBootStrap() {
        this(new MinicatBootstrapConfig());
    }

    public MinicatBootStrap(MinicatBootstrapConfig config) {
        this.config = config;
        // 创建组件
        readyComponents();
    }

    /**
     * 初始化
     */
    public void init() {
        // 组件初始化
        initComponent();
    }

    /**
     * 启动web服务器
     */
    public void start() {
        stopMark = false;
        masterThreadPool.execute(() -> {
            logger.info("minicat start on port " + config.getPort());
            while (!stopMark) {
                // 开始bio阻塞监听
                AcceptData accept = endPoint.accept();
                // 请求发生后交给worker线程处理
                workerThreadPool.execute(() -> work(accept));
            }
        });
    }

    /**
     * 停止web容器
     */
    public void stop() {
        stopMark = true;
    }

    private void work(AcceptData acceptData) {
        try (AcceptData accept = acceptData) {
            // 协议解析
            ProtocolData protocolData = this.protocol.protocol(accept);
            // 获取映射容器
            Context context = this.mapper.mapper(protocolData.getRequest().getUrl());
            if (context != null) {
                // 容器处理请求
                context.doContextMapper(protocolData);
            } else {
                protocolData.getResponse().notFound();
            }
        } catch (Throwable e) {
            logger.error("处理请求失败", e);
        }
    }

    private void readyComponents() {
        ServerStrategy serverStrategy = config.getServerStrategy();

        if (serverStrategy == null) {
            throw new MinicatException("serverStrategy cannot be null");
        }
        // 创建mapper
        mapper = new LocalStorageMapper();
        // 根据服务策略，创建endPoint和protocol组件
        switch (serverStrategy) {
            case HTTP_BIO:
                // 使用bio和http协议
                endPoint = new BioEndPoint(config.getPort());
                protocol = new HttpProtocol();
                break;
            default:
                throw new MinicatException(MessageFormat.format("serverStrategy {0} not supported", serverStrategy));
        }
        // 设置master线程池
        masterThreadPool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(), new MinicatThreadFactory());
        // 设置worker线程池
        workerThreadPool = new ThreadPoolExecutor(5, config.getMaxThread(), 0, TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(), new MinicatThreadFactory());
    }

    private void initComponent() {
        // 组件初始化工作
        endPoint.init();
        mapper.load(config.getWebAppsBasePackage());
    }


}
