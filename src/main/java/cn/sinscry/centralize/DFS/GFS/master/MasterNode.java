package cn.sinscry.centralize.DFS.GFS.master;

import cn.sinscry.common.api.ChunkServerApi;
import cn.sinscry.common.pojo.ChunkVo;
import cn.sinscry.common.utils.HeartbeatUtils;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MasterNode extends UnicastRemoteObject {
    private final List<String> chunkServerList;
    private final Map<String, List<ChunkVo>> serverChunkMap;
    public MasterNode() throws RemoteException {
        this.chunkServerList = new ArrayList<>();
        this.serverChunkMap = new LinkedHashMap<>();
        new HeartbeatUtils.HeartbeatCheckThread(this);
    }

    public synchronized void heartbeatScan(){
        System.out.println("Heartbeat checking");
        // Failing server list
        List<String> failedChunkServerList = new ArrayList<>();
        // Failing Chunk list
        Map<String, List<ChunkVo>> failedChunkMap = new LinkedHashMap<>();

        ChunkServerApi chunkServerApi;
        Map<Long, String> chunkHashMap;
        for(String chunkServer:chunkServerList){
            // check DataNode's heartbeat
            try{
                chunkHashMap = ((ChunkServerApi) Naming.lookup("rmi://" + chunkServer + "/chunkServer")).getMap();
                try{
                    List<ChunkVo> failedChunkList = new ArrayList<>();
                    for(ChunkVo chunkVo:serverChunkMap.get(chunkServer)){
                        String chunkHash = chunkHashMap.get(chunkVo.getChunkId());
                        if(chunkHash==null || !chunkHash.equals(chunkVo.getHash())){
                            System.out.println("chunk: " + chunkVo.getChunkId()+" ERROR!");
                        }
                    }
                }catch (Exception e) {
                    System.out.println("check chunk fail...");
                }
            }catch (Exception e){
                System.out.println("ChunkServer: " + chunkServer + " is down!");
                failedChunkServerList.add(chunkServer);
            }
        }

        handleFaults(failedChunkServerList);
        System.out.println("Heartbeat check end...");
    }

    private void handleFaults(List<String> failedChunkServerList){
        for(String failedServer : failedChunkServerList){
            System.out.println(failedServer+" chunkServer is down");
        }
    }

}
