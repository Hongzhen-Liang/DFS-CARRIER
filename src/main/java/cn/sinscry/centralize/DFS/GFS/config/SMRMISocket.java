package cn.sinscry.centralize.DFS.GFS.config;

import cn.sinscry.common.utils.ConfigUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;

public class SMRMISocket extends RMISocketFactory {

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return new Socket(host, port);
    }

    @Override
    public ServerSocket createServerSocket(int port) throws IOException {
        if (port == 0) {
            port = ConfigUtils.MASTER_DATA_PORT;
        }
        return new ServerSocket(port);
    }
}
