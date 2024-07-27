package cn.sinscry.decentralize.dht.POJO;

import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHashRing {
    private final SortedMap<Integer, DHTNode> nodeRing;

    // for availability
    private int replicationFactor = 1;

    public ConsistentHashRing(){
        this.nodeRing = new TreeMap<>();
    }
    public ConsistentHashRing(int replicationFactor){
        this();
        this.replicationFactor = replicationFactor;
    }

    public void put(String key, String value){
        for(int i=0; i<this.replicationFactor; i++){
            getNodeForKey(key).put(key, value);
            key = nextKey(key);
        }
    }
    private String nextKey(String key){
        return key + "_replica";
    }
    public String get(String key){
        return getNodeForKey(key).get(key);
    }
    public void addNode(DHTNode node){
        nodeRing.put(hashFunction(node.getNodeId()),node);
    }
    public DHTNode getNodeForKey(String key){
        int hash = hashFunction(key);
        if(nodeRing.isEmpty()){
            return null;
        }
        // this is the key idea: find the nearby node;
        if(!nodeRing.containsKey(hash)){
            SortedMap<Integer, DHTNode> tailMap = nodeRing.tailMap(hash);
            hash = tailMap.isEmpty() ? nodeRing.firstKey() : tailMap.firstKey();
        }
        return nodeRing.get(hash);
    }
    private int hashFunction(String key){
        return key.hashCode();
    }
}
