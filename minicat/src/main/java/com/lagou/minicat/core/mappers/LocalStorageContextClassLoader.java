package com.lagou.minicat.core.mappers;

import com.lagou.minicat.common.MinicatConstants;
import com.lagou.minicat.exceptions.MinicatException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用类加载器
 * 该应用类加载器，实际上并不是一个类加载器，而是封装了一个URL类加载器
 * 并封装了URL类加载器的初始化逻辑
 * 会从web应用程序目录下查找classes目录和lib目录
 * 前者是主要应用程序使用的类，后者则是应用程序依赖的三方jar包
 * 每个web应用程序都会有自己独立的MinicatAppContextClassLoader用来加载和管理程序所使用到的类
 *
 * @author wlz
 * @date 2020/5/12
 */
public class LocalStorageContextClassLoader {

    private URLClassLoader urlClassLoader;
    private ClassLoader parent;
    private String appBasePackage;

    public LocalStorageContextClassLoader(String appBasePackage, ClassLoader parent) {
        this.parent = parent;
        if (appBasePackage == null) {
            throw new MinicatException("appBasePackage cannot be null");
        }
        this.appBasePackage = appBasePackage;
        // 初始化类加载器
        initClassLoader();
    }

    private void initClassLoader() {
        // 获取到classes类路径下URL
        String classesDir = appBasePackage + File.separator + MinicatConstants.CLASSES_DIR_NAME;
        URL classUrl = parseClassUrl(classesDir);
        // 获取lib包下所有jar的URL
        String libDir = appBasePackage + File.separator + MinicatConstants.LIB_DIR_NAME + File.separator;
        List<URL> listUrls = parseLibJarUrlList(libDir);
        // 合并
        if (classUrl != null) {
            listUrls.add(classUrl);
        }
        // 创建urlClassLoader
        urlClassLoader = new URLClassLoader(listUrls.toArray(new URL[]{}), parent);
    }


    private URL parseClassUrl(String classesDir) {
        File classesFile = new File(classesDir);
        // 如果文件不存在，或者不是一个文件夹，或者是一个空文件夹，则直接返回
        if (!classesFile.exists() || !classesFile.isDirectory() || classesFile.listFiles() == null) {
            return null;
        }
        try {
            // 直接返回classes文件夹的Url
            return new URL(MinicatConstants.FILE_URL_PROTOCOL, null, classesFile.getCanonicalPath() + File.separator);
        } catch (IOException e) {
            throw new MinicatException("create classes url failed", e);
        }
    }

    private List<URL> parseLibJarUrlList(String libDir) {
        File libFile = new File(libDir);
        File[] jarFiles;
        // 如果文件不存在，或者不是一个文件夹，或者是一个空文件夹，则直接返回
        if (!libFile.exists() || !libFile.isDirectory() || (jarFiles = libFile.listFiles()) == null) {
            return new ArrayList<>(1);
        }
        // 从该lib文件夹下获取到所有的jar的URL
        List<URL> urlList = new ArrayList<>(jarFiles.length);
        try {
            for (File jarFile : jarFiles) {
                String canonicalPath = jarFile.getCanonicalPath();
                if (canonicalPath.endsWith(MinicatConstants.JAR_SUFFIX)) {
                    URL jarUrl = new URL(MinicatConstants.FILE_URL_PROTOCOL, null, canonicalPath);
                    urlList.add(jarUrl);
                }
            }
        } catch (IOException e) {
            throw new MinicatException("create classes url failed", e);
        }
        return urlList;
    }


    public URLClassLoader getAppClassLoader() {
        return urlClassLoader;
    }
}
