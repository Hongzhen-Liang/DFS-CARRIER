package cn.sinscry.decentralize.dht.POJO;

import com.google.common.collect.Maps;

import java.util.Map;

public class DHTNode {
    private final String nodeId;
    private final Map<String, String> keyValueStore;

    public DHTNode(int nodeId){
        this(String.valueOf(nodeId));
    }
    public DHTNode(String nodeId){
        this.nodeId = nodeId;
        this.keyValueStore = Maps.newHashMap();
    }
    public void put(String key, String value){
        this.keyValueStore.put(key, value);
    }
    public String get(String key){
        return keyValueStore.get(key);
    }
    public String getNodeId(){
        return nodeId;
    }
}
