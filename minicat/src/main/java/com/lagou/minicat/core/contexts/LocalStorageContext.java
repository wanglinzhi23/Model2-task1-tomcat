package com.lagou.minicat.core.contexts;

import com.lagou.minicat.core.mappers.LocalStorageMapper;
import com.lagou.minicat.common.entity.MyHttpResponse;
import com.lagou.minicat.common.entity.ProtocolData;
import com.lagou.minicat.common.entity.ServletMapperData;
import com.lagou.minicat.core.Context;
import com.lagou.servletx.MyRequest;
import com.lagou.servletx.MyResponse;
import com.lagou.servletx.MyServlet;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 本地存储容器
 * 该容器一般由LocalStorageMapper解析创建
 * 表示在本地解析得到的容器
 *
 * @author wlz
 * @date 2020/4/18
 * @see LocalStorageMapper 本地存储映射
 */
@Data
public class LocalStorageContext implements Context {

    /**
     * app容器名称
     */
    private String contextName;
    /**
     * app容器绝对路径
     */
    private String contextAbsolutePath;
    /**
     * app容器加载器
     */
    private ClassLoader appContextClassLoader;
    /**
     * servlet映射集合
     */
    private List<ServletMapperData> servletMapperData;
    /**
     * 静态资源映射集合
     */
    private Map<String, String> staticResources;


    public LocalStorageContext(String contextName, String contextAbsolutePath) {
        this.contextName = contextName;
        this.contextAbsolutePath = contextAbsolutePath;
    }

    @Override
    public void doContextMapper(ProtocolData protocolData) {
        // 1.解析请求路径
        MyRequest request = protocolData.getRequest();
        String url = request.getUrl();
        String requestResourceUrl = url.substring(contextName.length() + 1);
        // 2.处理静态资源文件
        doResponseForStaticResource(protocolData, requestResourceUrl);
        // 3.处理servlet
        doResponseForServlet(protocolData, requestResourceUrl);
        // 4.处理404
        doResponseForNotFound(protocolData);
    }

    private void doResponseForServlet(ProtocolData protocolData, String requestResourceUrl) {
        if (protocolData.isResponsed()) {
            return;
        }
        if (this.servletMapperData == null) {
            return;
        }
        MyRequest request = protocolData.getRequest();
        MyResponse response = protocolData.getResponse();
        // 先找到匹配到的servlet
        MyServlet myServlet = null;
        for (ServletMapperData servletMapperData : this.servletMapperData) {
            if (servletMapperData.getUrlPattern().matcher(requestResourceUrl).matches()) {
                myServlet = servletMapperData.getMyServlet();
            }
        }
        // 调用servlet处理
        if (myServlet != null) {
            myServlet.doService(request, response);
            protocolData.setResponsed(true);
        }

    }

    private void doResponseForStaticResource(ProtocolData protocolData, String requestResourceUrl) {
        if (protocolData.isResponsed()) {
            return;
        }
        MyResponse response = protocolData.getResponse();
        String staticContent = staticResources.get(requestResourceUrl);
        if (staticContent != null) {
            response.write(staticContent);
            response.flush();
            protocolData.setResponsed(true);
        }
    }

    private void doResponseForNotFound(ProtocolData protocolData) {
        if (protocolData.isResponsed()) {
            return;
        }
        MyHttpResponse response = (MyHttpResponse) protocolData.getResponse();
        response.notFound();
        protocolData.setResponsed(true);
    }
}
