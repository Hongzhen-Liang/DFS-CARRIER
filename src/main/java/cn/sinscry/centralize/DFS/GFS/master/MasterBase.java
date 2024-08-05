package cn.sinscry.centralize.DFS.GFS.master;

import cn.sinscry.common.api.ChunkServerApi;
import cn.sinscry.common.pojo.ChunkVo;
import cn.sinscry.common.utils.HeartbeatCheckThread;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MasterBase extends UnicastRemoteObject {
    private final List<String> chunkServerList;
    private final Map<String, List<ChunkVo>> serverChunkMap;
    private final List<NameNode> nameNodeList;
    public MasterBase() throws RemoteException {
        this.chunkServerList = new ArrayList<>();
        this.serverChunkMap = new LinkedHashMap<>();
        this.nameNodeList = new ArrayList<>();
        new HeartbeatCheckThread(this);
    }

    public synchronized void heartbeatScan(){
        System.out.println("Heartbeat checking");
        // Failing server list
        List<String> failedChunkServerList = new ArrayList<>();
        // Failing Chunk list detail
        Map<String, List<ChunkVo>> failedChunkMap = new LinkedHashMap<>();

        Map<Long, String> chunkHashMap;
        int index = 0;
        for(String chunkServer:chunkServerList){
            // use RMI to check DataNode's heartbeat
            try{
                chunkHashMap = ((ChunkServerApi) Naming.lookup("rmi://" + chunkServer + "/chunkServer")).getMap();
                try{
                    List<ChunkVo> failedChunkList = new ArrayList<>();
                    for(ChunkVo chunkVo:serverChunkMap.get(chunkServer)){
                        String chunkHash = chunkHashMap.get(chunkVo.getChunkId());
                        if(chunkHash==null || !chunkHash.equals(chunkVo.getHash())){
                            System.out.println("chunk: " + chunkVo.getChunkId() + " ERROR!");
                            chunkVo.removeReplicaServerName(chunkServer);
                            int idx = nameNodeList.indexOf(chunkVo.getNameNode());
                            nameNodeList.get(idx).getChunkVoList().set(chunkVo.getSeq(), chunkVo);
                            serverChunkMap.get(chunkServer).set(index, chunkVo);

                            failedChunkList.add(chunkVo);
                        }
                        index++;
                    }
                    failedChunkMap.put(chunkServer, failedChunkList);
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
