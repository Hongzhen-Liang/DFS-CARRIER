package cn.sinscry.decentralize.blockchain;

import cn.sinscry.decentralize.blockchain.Service.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        List<Integer> nodes = asList(9527, 9528);
        List<Server> serverList = new ArrayList<>();
        for(int port:nodes){
            Server server = new Server(port, nodes);
            server.start_server();
            serverList.add(server);
        }
    }
}