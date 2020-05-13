package com.lagou.minicat.common.entity;

import com.lagou.minicat.enums.HttpResponseCode;
import com.lagou.minicat.exceptions.MinicatException;
import com.lagou.servletx.MyResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * http响应
 *
 * @author wlz
 * @date 2020/5/12
 */
public class MyHttpResponse implements MyResponse {
    private OutputStream originOutputStream;
    private ByteArrayOutputStream tempOutputStream;
    private HttpResponseCode responseCode;
    private Map<String, String> headers;

    public MyHttpResponse(OutputStream originOutputStream) {
        this.originOutputStream = originOutputStream;
        tempOutputStream = new ByteArrayOutputStream();
        headers = new HashMap<>();
    }

    /**
     * 添加响应头信息
     *
     * @param name  名称
     * @param value 值
     */
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void setResponseCode(HttpResponseCode responseCode) {
        this.responseCode = responseCode;
    }


    @Override
    public void write(String data) {
        try {
            tempOutputStream.write(data.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new MinicatException("write data failed", e);
        }
    }

    @Override
    public OutputStream getOutputStream() {

        return tempOutputStream;
    }

    @Override
    public void flush() {
        try (OutputStream origin = originOutputStream;
             ByteArrayOutputStream temp = tempOutputStream) {
            // 写入内容数据
            byte[] content = temp.toByteArray();
            // 结束准备响应
            finishReadyResponse(content.length);
            origin.write(content);
        } catch (IOException e) {
            throw new MinicatException("write data failed", e);
        }
    }

    @Override
    public void notFound() {
        setResponseCode(HttpResponseCode.HTTP_NOT_FOUND);
        flush();
    }

    private void finishReadyResponse(long length) {
        // 1.设置响应码
        writeResponseCode();
        // 2.添加header
        writeHeaders();
        // 3.设置contentLength
        writeContentLength(length);
        // 4.添加空行
        writeBlankLine();
    }

    private void writeContentLength(long length) {
        String contentLengthFormat = "Content-Length: {0} \r\n";
        writeOriginData(MessageFormat.format(contentLengthFormat, String.valueOf(length)));
    }

    private void writeBlankLine() {
        writeOriginData("\r\n");
    }

    private void writeHeaders() {
        String headerLineFormat = "{0}: {1} \r\n";
        // 先设置contentType
        headers.putIfAbsent("Accept-Ranges", "bytes");
        headers.putIfAbsent("Content-Type", "text/html; charset=utf-8");
        // 写入所有的header
        headers.forEach((k, v) ->
                writeOriginData(MessageFormat.format(headerLineFormat, k, v))
        );
    }

    private void writeResponseCode() {
        if (responseCode == null) {
            responseCode = HttpResponseCode.HTTP_OK;
        }
        String responseCodeLine = MessageFormat.format("HTTP/1.1 {0} {1} \r\n",
                responseCode.getCode(), responseCode.getDisplay());
        writeOriginData(responseCodeLine);
    }


    private void writeOriginData(String data) {
        try {
            originOutputStream.write(data.getBytes());
        } catch (IOException e) {
            throw new MinicatException("write data filed", e);
        }
    }
}
