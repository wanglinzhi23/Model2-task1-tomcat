package com.lagou.minicat.core.mappers;

import com.lagou.minicat.common.MinicatConstants;
import com.lagou.minicat.common.entity.ServletMapperData;
import com.lagou.minicat.core.Context;
import com.lagou.minicat.core.Mapper;
import com.lagou.minicat.core.contexts.LocalStorageContext;
import com.lagou.minicat.exceptions.MinicatException;
import com.lagou.servletx.MyServlet;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 本地存储映射
 * 从指定本地路径获取映射关系
 *
 * @author wlz
 * @date 2020/5/12
 */
public class LocalStorageMapper implements Mapper {

    private Map<String, Context> contextMapper;

    @Override
    public void load(String webAppPackage) {
        File file = new File(webAppPackage);
        File[] appDirs;
        // 如果指定webapp路径不存在，不是文件夹，目录下没有文件，直接报错
        if (!file.exists() || !file.isDirectory() || (appDirs = file.listFiles()) == null) {
            throw new MinicatException(MessageFormat.format("webAppPackage {0} is not valid", webAppPackage));
        }
        // 初始化context的映射map
        contextMapper = new HashMap<>(appDirs.length);
        // 解析每一个目录，并为其创建web容器
        for (File appDir : appDirs) {
            LocalStorageContext context = buildContext(appDir);
            if (context != null) {
                contextMapper.put(context.getContextName(), context);
            }
        }
    }

    @Override
    public Context mapper(String url) {
        // 从url中获取请求的容器名称
        String contextName = getContextName(url);
        // 获取容器
        return contextMapper.get(contextName);
    }


    private LocalStorageContext buildContext(File appDir) {
        if (!appDir.exists() || !appDir.isDirectory() || appDir.listFiles() == null) {
            return null;
        }
        String appDirPath = appDir.getAbsolutePath();
        String contextName = appDir.getName();
        // 创建容器
        LocalStorageContext context = new LocalStorageContext(contextName, appDirPath);
        // 为该web容器创建类加载器
        buildContextClassLoader(context);
        // 构建servlet集合
        buildServletList(context);
        // 构建静态资源文件映射
        buildStaticResourceMapper(context);
        return context;
    }


    private void buildStaticResourceMapper(LocalStorageContext context) {
        String staticResourceDir = context.getContextAbsolutePath() + File.separator + MinicatConstants.STATIC_RESOURCE_DIR_NAME + File.separator;
        File staticFileDir = new File(staticResourceDir);
        File[] staticFiles;
        if (!staticFileDir.exists() || !staticFileDir.isDirectory() || (staticFiles = staticFileDir.listFiles()) == null) {
            return;
        }
        context.setStaticResources(new HashMap<>(staticFiles.length));
        // 递归加载所有静态资源文件
        loadStaticFiles(staticFiles, "", context.getStaticResources());
    }

    private void loadStaticFiles(File[] staticFiles, String baseDir, Map<String, String> staticResources) {
        if (staticFiles == null) {
            return;
        }
        for (File staticFile : staticFiles) {
            String staticResourceName = baseDir + File.separator + staticFile.getName();
            if (staticFile.isDirectory()) {
                // 如果是文件夹，则需要递归加载
                loadStaticFiles(staticFile.listFiles(), staticResourceName, staticResources);
            } else {
                // 如果是文件，则将文件流加载到内存中并添加到映射资源中
                try (FileInputStream fis = new FileInputStream(staticFile)) {
                    int len = fis.available();
                    byte[] fileContent = new byte[len];
                    fis.read(fileContent);
                    String content = new String(fileContent, StandardCharsets.UTF_8);
                    staticResources.put(staticResourceName, content);
                } catch (IOException e) {
                    throw new MinicatException(MessageFormat.format("load static file {0} failed", staticFile.getName()), e);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void buildServletList(LocalStorageContext context) {
        String webXmlPath = context.getContextAbsolutePath() + File.separator + MinicatConstants.WEB_XML_FILE;
        File file = new File(webXmlPath);
        // 如果文件不存在或者是一个文件夹，则不需要解析处理
        if (!file.exists() || file.isDirectory()) {
            return;
        }
        ClassLoader appContextClassLoader = context.getAppContextClassLoader();
        // 解析web.xml,获取到所有的servlet
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(file);
            Node webNode = document.selectSingleNode("web");
            // 获取到所有servlet标签
            List<Node> servletNodes = webNode.selectNodes("servlet");
            ArrayList<ServletMapperData> servletList = new ArrayList<>(servletNodes.size());
            // 解析所有servlet标签
            for (Node servletNode : servletNodes) {
                Node patternNode = servletNode.selectSingleNode("servlet-pattern");
                Node classNode = servletNode.selectSingleNode("servlet-class");
                String pattern = patternNode.getText().trim();
                String className = classNode.getText().trim();
                Class<? extends MyServlet> servletClass = (Class<? extends MyServlet>) Class.forName(className, false, appContextClassLoader);
                MyServlet myServlet = servletClass.newInstance();
                Pattern servletPattern = Pattern.compile(pattern);
                ServletMapperData servletMapperData = new ServletMapperData(servletPattern, myServlet);
                servletList.add(servletMapperData);
            }
            context.setServletMapperData(servletList);
        } catch (Exception e) {
            throw new MinicatException("parser web.xml failed", e);
        }

    }

    private void buildContextClassLoader(LocalStorageContext context) {
        String contextAbsolutePath = context.getContextAbsolutePath();
        URLClassLoader appClassLoader = new LocalStorageContextClassLoader(contextAbsolutePath, this.getClass().getClassLoader())
                .getAppClassLoader();
        context.setAppContextClassLoader(appClassLoader);
    }


    private String getContextName(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        String[] split = url.split(MinicatConstants.URL_SEPARATOR);
        return split[1];
    }
}
