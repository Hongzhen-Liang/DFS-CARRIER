package cn.sinscry.centralize.DFS.GFS.master;

import cn.sinscry.common.pojo.ChunkVo;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NameNode implements Serializable {
    private final String nodeName;
    private final List<ChunkVo> chunkVos;

    public NameNode(String fileName){
        this.nodeName = fileName;
        chunkVos = new ArrayList<>();
    }

    public String getNodeName(){
        return nodeName;
    }

    public List<ChunkVo> getChunkVos(){
        return this.chunkVos;
    }

}
