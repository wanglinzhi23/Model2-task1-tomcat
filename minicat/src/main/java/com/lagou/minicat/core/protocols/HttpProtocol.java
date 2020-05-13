package com.lagou.minicat.core.protocols;

import com.lagou.minicat.core.Protocol;
import com.lagou.minicat.common.entity.AcceptData;
import com.lagou.minicat.common.entity.ProtocolData;
import com.lagou.minicat.common.entity.MyHttpRequest;
import com.lagou.minicat.common.entity.MyHttpResponse;
import com.lagou.minicat.exceptions.MinicatException;
import com.lagou.servletx.RequestMethodTypeEnum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * http协议实现
 *
 * @author wlz
 * @date 2020/5/12
 */
public class HttpProtocol implements Protocol {

    @Override
    public ProtocolData protocol(AcceptData acceptData) {
        // 解析httpRequest对象
        MyHttpRequest httpRequest = parseHttpRequest(acceptData);
        // 解析httpResponse对象
        MyHttpResponse httpResponse = parseHttpResponse(acceptData);
        return new ProtocolData(httpRequest, httpResponse, false);
    }

    private MyHttpResponse parseHttpResponse(AcceptData acceptData) {

        return new MyHttpResponse(acceptData.getResponseOutputStream());
    }

    private MyHttpRequest parseHttpRequest(AcceptData acceptData) {

        InputStream requestInputStream = acceptData.getRequestInputStream();
        try {
            while (requestInputStream.available() == 0) {
                TimeUnit.MILLISECONDS.sleep(20);
            }
        } catch (Throwable e) {
            throw new MinicatException("parse request failed", e);
        }
        BufferedReader buf = new BufferedReader(new InputStreamReader(requestInputStream));
        // 构建httpRequest对象
        MyHttpRequest myHttpRequest = new MyHttpRequest();
        // 读第一行数据,设置请求方式，请求url，请求参数
        readFirstLine(buf, myHttpRequest);
        return myHttpRequest;
    }

    private void readFirstLine(BufferedReader buf, MyHttpRequest myHttpRequest) {
        String line = readLine(buf);
        String[] split = line.split(" ");
        String methodType = split[0];
        RequestMethodTypeEnum methodTypeEnum = RequestMethodTypeEnum.getByName(methodType);
        // 设置请求type
        myHttpRequest.setMethodType(methodTypeEnum);
        // 设置url
        String[] urlAndParam = split[1].split("\\?");
        myHttpRequest.setUrl(urlAndParam[0]);
        if (urlAndParam.length > 1) {
            // 解析url参数
            parseParam(urlAndParam[1], myHttpRequest);
        }
    }

    private void parseParam(String paramLine, MyHttpRequest myHttpRequest) {
        String[] split = paramLine.split("&");
        for (String paramEntry : split) {
            String[] keyValue = paramEntry.split("=");
            myHttpRequest.addParam(keyValue[0], keyValue[1]);
        }
    }

    private String readLine(BufferedReader buf) {
        String line;
        try {
            line = buf.readLine();
        } catch (IOException e) {
            throw new MinicatException("read data failed ", e);
        }
        return line;
    }
}
