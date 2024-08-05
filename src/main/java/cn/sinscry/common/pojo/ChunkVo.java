package cn.sinscry.common.pojo;

import cn.sinscry.centralize.DFS.GFS.master.NameNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ChunkVo implements Serializable {
    /** file's sequence number */
    private int seq;
    private long chunkId;
    private String hash;
    private NameNode nameNode;
    private String fileName;

    /** chunk's replica server */
    private final Set<String> replicaServerName;

    public ChunkVo(String fileName, long chunkId, long byteSize, int seq, String hash){
        this.chunkId = chunkId;
        // two copy
        this.replicaServerName = new HashSet<>();
        this.seq = seq;
        this.hash = hash;
        this.fileName = fileName;
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

    public Set<String> getReplicaServerName(){
        return this.replicaServerName;
    }

    /** return a valid replica server */
    public String removeReplicaServerName(String serverName){
        replicaServerName.remove(serverName);
        return replicaServerName.stream().toList().getFirst();
    }

    public void setNameNode(NameNode nameNode){
        this.nameNode = nameNode;
    }

    public String getFileName(){
        return this.fileName;
    }

}