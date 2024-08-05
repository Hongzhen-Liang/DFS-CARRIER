package cn.sinscry.centralize.DFS.GFS.api;

import cn.sinscry.common.pojo.ChunkVo;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MasterApi extends Remote {
    void registerChunkServer(String ip, int workerPort) throws Exception;
    void addNameNode(String fileName) throws RemoteException;
    ChunkVo addChunk(String fileName, int seq, long length, String hash) throws RemoteException;
}