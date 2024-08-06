package cn.sinscry.centralize.DFS.GFS.api;

import cn.sinscry.common.pojo.ChunkVo;

import java.io.FileNotFoundException;
import java.rmi.Remote;
import java.util.List;
import java.util.Map;

public interface WorkerApi extends Remote {
    Map<Long, String> getMap() throws Exception;
    void backupChunk(ChunkVo chunkVo, String serverName) throws Exception;
    boolean pushChunk(ChunkVo chunkVo, byte[] bytes, List<String> replicaServerName) throws Exception;
    byte[] getChunk(ChunkVo chunkVo) throws Exception;
    List<String> deleteChunk(ChunkVo chunkVo, List<String> replicaServerNames) throws Exception;
    String getHash(ChunkVo chunkVo) throws Exception;
    boolean updateChunk(ChunkVo chunkVo, byte[] bytes, List<String> replicaServerNames) throws Exception;
}
