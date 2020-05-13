package com.lagou.minicat.test;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

/**
 * WebXMlParseTest
 *
 * @author wlz
 * @date 2020/4/18
 */
public class WebXMlParseTest {
    public static void main(String[] args) throws DocumentException {
        InputStream stream = WebXMlParseTest.class.getClassLoader().getResourceAsStream("web.xml");
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(stream);
        Node webNode = document.selectSingleNode("web");
        List<Node> servlets = webNode.selectNodes("servlet");
        for (Node servlet : servlets) {
            System.out.println(servlet);
        }
    }
}
