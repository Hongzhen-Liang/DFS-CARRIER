package cn.sinscry.centralize.DFS.GFS.api;

import cn.sinscry.common.pojo.ChunkVo;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface MasterApi extends Remote {
    void registerChunkServer(String ip, int workerPort) throws Exception;
    boolean addNameNode(String fileName) throws Exception;
    ChunkVo addChunk(String fileName, int seq, long length, String hash) throws RemoteException;
    List<ChunkVo> getChunks(String fileName) throws RemoteException;
    boolean deleteNameNode(String fileName) throws Exception;
    List<String> getFileList() throws Exception;
    void updateNameNode(String fileName, long length) throws Exception;
}
