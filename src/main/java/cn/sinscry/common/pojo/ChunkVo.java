package cn.sinscry.common.pojo;

import java.io.Serializable;

public class ChunkVo implements Serializable {
    private long chunkId;
    private String hash;
    public long getChunkId(){
        return chunkId;
    }
    public String getHash(){
        return hash;
    }
}
