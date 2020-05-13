package com.lagou.minicat.core.endpoints;

import com.lagou.minicat.common.entity.AcceptData;
import com.lagou.minicat.core.EndPoint;
import com.lagou.minicat.exceptions.MinicatException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * bio传输层实现
 *
 * @author wlz
 * @date 2020/5/12
 */
public class BioEndPoint implements EndPoint {

    private int port;
    private ServerSocket serverSocket;

    public BioEndPoint(int port) {
        this.port = port;
    }

    @Override
    public void init() {
        try {
            serverSocket = new ServerSocket(port);

        } catch (IOException e) {
            throw new MinicatException("create server socket failed", e);
        }
    }

    @Override
    public AcceptData accept() {
        try {
            Socket accept = serverSocket.accept();
            // 获取到socket的输入输出流
            InputStream inputStream = accept.getInputStream();
            OutputStream outputStream = accept.getOutputStream();
            // 获取远程主机ip
            String host = getRemoteHost(accept);
            // 返回接收的数据
            return new AcceptData()
                    .setRequestInputStream(inputStream)
                    .setResponseOutputStream(outputStream)
                    .setRemoteHost(host)
                    .setSocket(accept);
        } catch (IOException e) {
            throw new MinicatException("create server socket failed", e);
        }
    }

    private String getRemoteHost(Socket accept) {
        SocketAddress socketAddress = accept.getRemoteSocketAddress();
        return socketAddress.toString();
    }
}
