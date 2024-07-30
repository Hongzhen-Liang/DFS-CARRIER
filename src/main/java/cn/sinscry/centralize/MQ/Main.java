package cn.sinscry.centralize.MQ;

import cn.sinscry.centralize.MQ.Service.MqClient;

public class Main {
    public static void main(String[] args) throws Exception {
        String sendMsg = "SEND:Hello World!";
        MqClient.produce(sendMsg);
        String receiveMsg = MqClient.consume();
        System.out.printf("receive message:%s\n", receiveMsg);
    }
}
