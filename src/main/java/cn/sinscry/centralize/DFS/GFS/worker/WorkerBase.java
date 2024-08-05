package cn.sinscry.centralize.DFS.GFS.worker;

import cn.sinscry.centralize.DFS.GFS.api.ChunkServerApi;
import cn.sinscry.centralize.DFS.GFS.api.MasterApi;
import cn.sinscry.common.pojo.ChunkVo;
import cn.sinscry.common.utils.SecurityUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorkerBase extends UnicastRemoteObject implements ChunkServerApi {
    private final String currentIpAddr;
    private final List<Long> chunkIdList;
    private final Map<Long, String> chunkHash;
    private final String prefixPath;

    public WorkerBase(String masterIp, int masterPort, int wokerPort, String prefixPath) throws Exception {
        chunkIdList = new ArrayList<>();
        chunkHash = new ConcurrentHashMap<>();
        this.prefixPath =prefixPath;
        currentIpAddr = InetAddress.getLocalHost().getHostAddress();
        MasterApi master = (MasterApi) Naming.lookup("rmi://" + masterIp + ":" + masterPort + "/master");
        master.registerChunkServer(currentIpAddr, wokerPort);
    }

    public String getCurrentIpAddr(){
        return currentIpAddr;
    }

    @Override
    public Map<Long, String> getMap() throws Exception {
        return null;
    }

    @Override
    public void backupChunk(ChunkVo chunkVo, String serverName) throws Exception {

    }

    @Override
    public boolean pushChunk(ChunkVo chunkVo, byte[] bytes, List<String> replicaServerName) throws Exception{
        String hash = saveChunkFile(chunkVo, bytes);
        chunkIdList.add(chunkVo.getChunkId());
        chunkHash.put(chunkVo.getChunkId(), hash);
        System.out.println("chunk "+chunkVo.getChunkId()+" added");
        if(!replicaServerName.isEmpty()){
            ((ChunkServerApi) Naming.lookup("rmi://" + replicaServerName.removeFirst() + "/worker")).pushChunk(chunkVo, bytes, replicaServerName);
        }
        return true;
    }

        private String saveChunkFile(ChunkVo chunkVo, byte[] bytes) throws Exception{
        String filePath = prefixPath + chunkVo.getFileName()+"_"+chunkVo.getChunkId();
        File file = new File(filePath);
        if(!file.exists()){
            file.createNewFile();
        }
        OutputStream output = new FileOutputStream(file, false);
        output.write(bytes);
        output.close();
        return SecurityUtil.getMd5(filePath);
    }

}
