package cn.sinscry.centralize.MQ.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/** Client for Message queue */
public class MqClient {
    // produce message
    public static void produce(String msg) throws Exception{
        try(
            Socket socket = new Socket(InetAddress.getLocalHost(), BrokerServer.SERVICE_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream());
        ){
            out.println(msg);
            out.flush();
        }
    }

    // consume message
    public static String consume() throws Exception{
        try(
            Socket socket = new Socket(InetAddress.getLocalHost(), BrokerServer.SERVICE_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());
        ){
            out.println("CONSUME");
            out.flush();
            return in.readLine();
        }
    }

}
