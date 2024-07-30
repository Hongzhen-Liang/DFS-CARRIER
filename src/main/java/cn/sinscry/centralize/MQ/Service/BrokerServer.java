package cn.sinscry.centralize.MQ.Service;

import cn.sinscry.centralize.MQ.POJO.Broker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class BrokerServer implements Runnable{
    public static int SERVICE_PORT = 9999;

    private final Socket socket;

    public BrokerServer(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        try(BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream())) {
            while (true){
                String msg = in.readLine();
                if(Objects.isNull(msg)){
                    continue;
                }
                System.out.printf("receive msg:%s\n", msg);
                if(msg.equals("CONSUME")){
                    out.println(Broker.consume());
                    out.flush();
                }else if(msg.contains("SEND:")){
                    Broker.produce(msg);
                }else{
                    System.out.printf("incorrect msg format:%s\n", msg);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        try(
            ServerSocket server = new ServerSocket(SERVICE_PORT)
        ){
            System.out.println("Start a BrokerServer");
            while (true){
                BrokerServer brokerServer = new BrokerServer(server.accept());
                new Thread(brokerServer).start();
            }
        }
    }
}
