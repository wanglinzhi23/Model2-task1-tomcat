package com.lagou.minicat.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * TestJarLoader
 *
 * @author wlz
 * @date 2020/4/18
 */
public class TestJarLoader {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        File lib = new File("/Users/webapps/app1/classes");
        URL classPath = new URL("file", null, lib.getCanonicalPath()+File.separator);
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{classPath});
        Class<?> aClass = Class.forName("com.lagou.niancheng.app1.LoginServlet", false, urlClassLoader);
        System.out.println(aClass);

    }
}
