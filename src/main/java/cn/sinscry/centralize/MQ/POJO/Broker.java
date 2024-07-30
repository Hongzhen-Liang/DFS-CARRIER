package cn.sinscry.centralize.MQ.POJO;

import com.google.common.collect.Lists;

import java.util.List;

/** message publisher */
public class Broker {
    // maximum queue size is 3
    private final static int MAX_SIZE = 3;
    // queue containers
    private static List<String> messageQueue = Lists.newArrayListWithCapacity(MAX_SIZE);

    // produce message
    public static void produce(String msg){
        if(messageQueue.size()<MAX_SIZE){
            messageQueue.add(msg);
            System.out.printf("msg %s was received successfully, queue size is %d now.\n",msg,messageQueue.size());
        }else{
            System.out.println("message queue size exceed");
        }
        System.out.println("===========================");
    }

    // consume message
    public static String consume(){
        if(messageQueue.isEmpty()){
            System.out.println("Empty queue has no message to consume");
            return null;
        }else{
            String msg = messageQueue.removeFirst();
            System.out.printf("consume msg:%s, queue size is %d now.\n",msg,messageQueue.size());
            return msg;
        }
    }


}
