package cn.sinscry.centralize.DFS.GFS.master;

import cn.sinscry.centralize.DFS.GFS.api.WorkerApi;
import cn.sinscry.centralize.DFS.GFS.api.MasterApi;
import cn.sinscry.common.pojo.ChunkVo;
import cn.sinscry.common.utils.HeartbeatCheckThread;
import com.google.common.collect.Lists;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MasterBase extends UnicastRemoteObject implements MasterApi {
    private final int replicaNum=3;
    private final List<String> wokerServerList;
    private final Map<String, List<ChunkVo>> serverChunkMap;
    private final List<NameNode> nameNodeList;
    public MasterBase() throws RemoteException {
        this.wokerServerList = new ArrayList<>();
        this.serverChunkMap = new LinkedHashMap<>();
        this.nameNodeList = new ArrayList<>();
        Thread heartbeatCheckThread = new HeartbeatCheckThread(this);
        heartbeatCheckThread.start();
    }

    public synchronized void heartbeatScan(){
//        System.out.println("Heartbeat checking");
        // Failing server list
        List<String> failedChunkServerList = new ArrayList<>();
        // Failing Chunk list detail
        Map<String, List<ChunkVo>> failedChunkMap = new LinkedHashMap<>();
        for(String chunkServer:wokerServerList){
            // use RMI to check DataNode's heartbeat
            try{
                Map<Long, String> chunkHashMap = ((WorkerApi) Naming.lookup("rmi://" + chunkServer + "/worker")).getMap();
                try{
                    // iterate all chunk within the server

                    for(ChunkVo chunkVo: Optional.ofNullable(serverChunkMap.get(chunkServer)).orElse(Lists.newArrayList())){
                        String chunkHash = chunkHashMap.get(chunkVo.getChunkId());
                        if(chunkHash==null || !chunkHash.equals(chunkVo.getHash())){
                            System.out.println("chunk: " + chunkVo.getChunkId() + " ERROR!");
                            List<ChunkVo> failedChunkList = failedChunkMap.get(chunkServer);
                            if(Objects.isNull(failedChunkList)){
                                failedChunkList = new ArrayList<>();
                                failedChunkMap.put(chunkServer, failedChunkList);
                            }
                            chunkVo.removeReplicaServerName(chunkServer);
                            failedChunkList.add(chunkVo);
                        }
                    }
                }catch (Exception e) {
                    System.out.println("check chunk fail...");
                }
            }catch (Exception e){
                System.out.println("Worker Server: " + chunkServer + " is down!");
                failedChunkServerList.add(chunkServer);
            }
        }

        handleFaults(failedChunkMap, failedChunkServerList);
//        System.out.println("Heartbeat check end...");
    }

    private void handleFaults(Map<String, List<ChunkVo>> failedChunkMap,List<String> failedChunkServerList){
        if(failedChunkMap.isEmpty() && failedChunkServerList.isEmpty()){
            System.out.println("Heartbeat check pass");
            return;
        }
        // situation 1: the whole server is down
        for(String failedServer : failedChunkServerList){
            System.out.println("processing down server: "+failedServer);
            if(serverChunkMap.get(failedServer)==null){
                wokerServerList.remove(failedServer);
                System.out.println("delete unused server");
                continue;
            }
            for(ChunkVo chunkVo : serverChunkMap.get(failedServer)){
                System.out.println("replicating " + failedServer + " server's chunk "+chunkVo.getChunkId());
                try{
                    String serverName = chunkVo.removeReplicaServerName(failedServer);
                    String replicaServerName = allocateNode(chunkVo, serverName);
                    ((WorkerApi) Naming.lookup(
                            "rmi://" + serverName + "/chunkServer")).backupChunk(chunkVo, replicaServerName);
                    System.out.println("finish solving down server");
                }catch (Exception e){
                    System.out.println("failed to processing server");
                    e.printStackTrace();
                }
            }
        }

        // situation 2: chunk error
        for(Map.Entry<String, List<ChunkVo>> failedChunk:failedChunkMap.entrySet()){
            String failedServer = failedChunk.getKey();
            for(ChunkVo chunkVo:failedChunk.getValue()){
                System.out.println("recovering chunk "+chunkVo.getChunkId()+" from server "+failedServer);
                try{
                    String serverName = chunkVo.removeReplicaServerName(failedServer);
                    if(serverName==null){
                        System.out.println("no replica error");
                        continue;
                    }
                    ((WorkerApi) Naming.lookup(
                            "rmi://" + serverName + "/chunkServer")).backupChunk(chunkVo, failedServer);
                }catch (Exception e){
                    System.out.println("recover failed");
                    e.printStackTrace();
                }
            }
        }
    }
    private String allocateNode(ChunkVo chunkVo, String serverName){
        int index = serverName==null ? 0:
            (wokerServerList.indexOf(serverName) + 1) % wokerServerList.size();
        String replicaServerName = wokerServerList.get(index);
        chunkVo.getReplicaServerName().add(replicaServerName);
        return replicaServerName;
    }

    @Override
    public void registerChunkServer(String ip, int workerPort) throws Exception {
        wokerServerList.add(ip+":"+workerPort);
        serverChunkMap.put(ip,new ArrayList<>());
        System.out.println("Worker " + ip+":"+workerPort+" added");
    }

    @Override
    public void addNameNode(String fileName){
        nameNodeList.add(new NameNode(fileName));
    }
    @Override
    public ChunkVo addChunk(String fileName, int seq, long length, String hash){
        String chunkName = new DecimalFormat("0000").format(seq);
        ChunkVo chunkVo = new ChunkVo(fileName,Long.parseLong(chunkName), length, seq, hash);

        setReplicaChunk(chunkVo);

        for(NameNode nameNode:nameNodeList){
            if(nameNode.getNodeName().equals(fileName)){
                nameNode.getChunkVos().add(chunkVo);
                chunkVo.setNameNode(nameNode);
                break;
            }
        }
        return chunkVo;
    }

    @Override
    public List<ChunkVo> getChunks(String fileName) throws RemoteException {
        for(NameNode nameNode:nameNodeList){
            if(nameNode.getNodeName().equals(fileName)){
                return nameNode.getChunkVos();
            }
        }
        return new ArrayList<>();
    }

    private void setReplicaChunk(ChunkVo chunkVo){
        String serverName = null;
        for(int i=0;i<replicaNum;i++){
            serverName = allocateNode(chunkVo, serverName);
        }
    }
}
