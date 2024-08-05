package cn.sinscry.common.pojo;

import cn.sinscry.centralize.DFS.GFS.master.NameNode;

import java.io.Serializable;

public class ChunkVo implements Serializable {
    /** file's sequence number */
    private int seq;
    private long chunkId;
    private String hash;
    private NameNode nameNode;

    /** chunk's replica server */
    private final String[] replicaServerName;

    public ChunkVo(long chunkId, long byteSize, int seq, String hash){
        this.chunkId = chunkId;
        // two copy
        this.replicaServerName = new String[2];
        this.seq = seq;
        this.hash = hash;
    }

    public long getChunkId(){
        return chunkId;
    }
    public String getHash(){
        return hash;
    }

    public int getSeq(){
        return seq;
    }

    public NameNode getNameNode(){
        return nameNode;
    }

    public void removeReplicaServerName(String serverName){
        if(replicaServerName[1]==null){
            return;
        }
        if(replicaServerName[0].equals(serverName)){
            replicaServerName[0]=replicaServerName[1];
        }
        replicaServerName[1] = null;
    }
}
