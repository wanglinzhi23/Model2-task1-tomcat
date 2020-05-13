package com.lagou.minicat.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * TestClassLoader
 *
 * @author wlz
 * @date 2020/5/12
 */
public class TestClassLoader {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        ResourceClassLoader resourceClassLoader = new ResourceClassLoader();
        Class<?> aClass = Class.forName("com.lagou.niancheng.entity.Article", true, resourceClassLoader);
        System.out.println(aClass.hashCode());
        ResourceClassLoader resourceClassLoader2 = new ResourceClassLoader();
        Class<?> aClass2 = Class.forName("com.lagou.niancheng.entity.Article", false, resourceClassLoader2);
        Object o1 = aClass.newInstance();
        Object o2 = aClass2.newInstance();
        System.out.println(o1.getClass() + "====>" + o1.getClass().hashCode());
        System.out.println(o2.getClass() + "====>" + o2.getClass().hashCode());
    }


    public static class ResourceClassLoader extends ClassLoader {

        private Map<String, Class> selfClassMap = new HashMap<>();

        private String basePackage = "/Users/mac/Documents/work/model1-task4-springboot2/target/classes/";

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            Class<?> loadedClass = findLoadedClass(name);
            if (loadedClass != null) {
                return loadedClass;
            }
            try {
                if (loadedClass != null) {
                    return loadedClass;
                }
                String classFilePath = basePackage + name.replaceAll("\\.", "/") + ".class";
                FileInputStream fileInputStream = new FileInputStream(classFilePath);
                int available = fileInputStream.available();
                byte[] bytes = new byte[available];
                fileInputStream.read(bytes);
                loadedClass = defineClass(name, bytes, 0, bytes.length);
                return loadedClass;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}
