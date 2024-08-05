package cn.sinscry.centralize.DFS.GFS.client;

import cn.sinscry.centralize.DFS.GFS.api.ChunkServerApi;
import cn.sinscry.centralize.DFS.GFS.api.MasterApi;
import cn.sinscry.common.pojo.ChunkVo;
import cn.sinscry.common.utils.SecurityUtil;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

public class ClientBase {

    /** 64MB */
    private static final int CHUNK_SIZE = 64 * 1024 * 1024;
    private final MasterApi masterApi;
    private final String prefixPath;


    public ClientBase(MasterApi masterApi, String prefixPath){
        this.masterApi = masterApi;
        this.prefixPath = prefixPath;

    }

    public void upLoadFile(String fileAddress){
        System.out.println("uploading file...");
        try{
            int length, seq=0;
            byte[] buffer = new byte[CHUNK_SIZE];
            File file = new File(fileAddress);
            masterApi.addNameNode(file.getName());

            InputStream input = new FileInputStream(file);
            input.skip(0);
            while((length = input.read(buffer, 0, CHUNK_SIZE))>0){
                byte[] upLoadBytes = new byte[length];
                System.arraycopy(buffer, 0, upLoadBytes, 0, length);
                String hash = SecurityUtil.getMd5(upLoadBytes);
                uploadChunk(file.getName(), seq, length, upLoadBytes, hash);
                seq++;
            }
            input.close();
            System.out.println("file uploaded!");
        } catch (Exception e) {
            System.out.println("file fail to upload");
            System.out.println(e.getLocalizedMessage());
        }
    }

    private void uploadChunk(String fileName, int seq, long length, byte[] bytes, String hash) throws Exception{
        ChunkVo chunkVo = masterApi.addChunk(fileName, seq, length, hash);
        List<String> replicaServerName = Lists.newArrayList(chunkVo.getReplicaServerName());
        ChunkServerApi chunkServerApi = (ChunkServerApi) Naming.lookup("rmi://" + replicaServerName.removeFirst() + "/worker");
        chunkServerApi.pushChunk(chunkVo, bytes, replicaServerName);
    }
}
