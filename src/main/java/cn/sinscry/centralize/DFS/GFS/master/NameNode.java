package cn.sinscry.centralize.DFS.GFS.master;

import cn.sinscry.common.pojo.ChunkVo;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NameNode implements Serializable {
    private final String nodeName;
    private final List<ChunkVo> chunkVoList;

    public NameNode(String fileName){
        this.nodeName = fileName;
        chunkVoList = new ArrayList<>();
    }

    public String getNodeName(){
        return nodeName;
    }

    public List<ChunkVo> getChunkVoList(){
        return this.chunkVoList;
    }
}
