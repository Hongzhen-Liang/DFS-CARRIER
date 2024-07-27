package cn.sinscry.decentralize.dht;

import cn.sinscry.decentralize.dht.POJO.DHTNode;
import cn.sinscry.decentralize.dht.POJO.ConsistentHashRing;

import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {
        ConsistentHashRing ring = new ConsistentHashRing();
        int nodeNum = 3;
        int keyNum = 3;
        // add storing node to ring
        for(int i=0;i<nodeNum;i++){
            ring.addNode(new DHTNode(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE)));
        }

        for(int i=0;i<keyNum;i++){
            int keyHash = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
            DHTNode node = ring.getNodeForKey(String.valueOf(keyHash));
            System.out.printf("key %d map to node %s\n", keyHash, node.getNodeId());
        }
    }
}
