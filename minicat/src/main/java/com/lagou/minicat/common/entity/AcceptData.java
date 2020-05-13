package com.lagou.minicat.common.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 接收的数据包
 *
 * @author wlz
 * @date 2020/5/12
 */
@Data
@Accessors(chain = true)
public class AcceptData implements Closeable {
    private String remoteHost;
    private InputStream requestInputStream;
    private OutputStream responseOutputStream;
    private Socket socket;

    @Override
    public void close() throws IOException {
        requestInputStream.close();
        responseOutputStream.close();
        if (!socket.isClosed()) {
            socket.close();
        }
    }
}
