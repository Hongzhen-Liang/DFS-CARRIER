package cn.sinscry.centralize.DFS.GFS.client;

import cn.sinscry.centralize.DFS.GFS.api.WorkerApi;
import cn.sinscry.centralize.DFS.GFS.api.MasterApi;
import cn.sinscry.common.pojo.ChunkVo;
import cn.sinscry.common.utils.ConvertUtil;
import cn.sinscry.common.utils.SecurityUtil;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.Naming;
import java.util.List;

import static cn.sinscry.common.utils.ConfigUtils.CHUNK_SIZE;

public class ClientBase {

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
        WorkerApi workerApi = (WorkerApi) Naming.lookup("rmi://" + replicaServerName.removeFirst() + "/worker");
        workerApi.pushChunk(chunkVo, bytes, replicaServerName);
    }

    public String downloadFile(String fileName) throws Exception{
        System.out.println("downloading files...");
        String fileAddress = prefixPath + "new_" + fileName;
        OutputStream output = new FileOutputStream(fileAddress);
        List<ChunkVo> chunkVos = masterApi.getChunks(fileName);
        for(ChunkVo chunkVo:chunkVos){
            byte[] downloadBytes = downloadChunk(chunkVo);
            if(!SecurityUtil.getMd5(downloadBytes).equals(chunkVo.getHash())){
                System.out.println(chunkVo.getChunkId()+"chunk is polluted");
                return "";
            }
            output.write(downloadBytes);
        }
        output.close();
        System.out.println(fileName+(chunkVos.isEmpty()?" files doesn't exist":" files downloaded success"));
        return fileAddress;
    }

    private byte[] downloadChunk(ChunkVo chunkVo) throws Exception {
        WorkerApi workerApi = (WorkerApi) Naming.lookup("rmi://" + chunkVo.getReplicaServerName().iterator().next() + "/worker");
        return workerApi.getChunk(chunkVo);
    }

    public boolean deleteFile(String fileName) throws Exception{
        boolean flag = masterApi.deleteNameNode(fileName);
        System.out.println("delete file "+(flag? "success":"failed"));
        return flag;
    }
}
