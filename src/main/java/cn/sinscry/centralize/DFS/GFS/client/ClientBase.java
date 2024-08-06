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
        File file = new File(fileAddress);
        boolean flag = false;
        try(InputStream input = new FileInputStream(file)){
            flag = masterApi.addNameNode(file.getName());
            if(flag){
                appendChunk(file.getName(), 0, input);
            }
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }finally {
            System.out.println(file.getName()+" upload "+(flag?"success":"fail: file already exists"));
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

    public List<String> getFileList() throws Exception{
        List<String> fileList = masterApi.getFileList();
        System.out.println(fileList);
        return fileList;
    }

    public void appendFile(String fileName, String appendFileAddress) throws Exception {
        try(InputStream input = new FileInputStream(appendFileAddress)) {
            List<ChunkVo> chunkInfoList = masterApi.getChunks(fileName);
            if (chunkInfoList.isEmpty()) {
                upLoadFile(appendFileAddress);
                return;
            }
            ChunkVo chunkVo = chunkInfoList.getLast();
            updateChunk(fileName, chunkVo, input);
            appendChunk(fileName, chunkVo.getSeq()+1, input);
        }
        System.out.println("append file success!");
    }

    private void updateChunk(String fileName, ChunkVo chunkVo, InputStream in) throws Exception {
        int remainSize=(int)(CHUNK_SIZE - chunkVo.getByteSize());
        if(remainSize>0){
            byte[] bytes = ConvertUtil.file2Byte(in, remainSize);
            List<String> replicaServerNames = Lists.newArrayList(chunkVo.getReplicaServerName());
            ((WorkerApi) Naming.lookup("rmi://" + replicaServerNames.removeFirst() + "/worker")).updateChunk(chunkVo, bytes, replicaServerNames);
            masterApi.updateNameNode(fileName, chunkVo.getByteSize() + bytes.length);
        }
    }

    private void appendChunk(String fileName, int seq, InputStream input) throws Exception {
        byte[] upLoadBytes = ConvertUtil.file2Byte(input, CHUNK_SIZE);
        while (upLoadBytes.length > 0) {
            String hash = SecurityUtil.getMd5(upLoadBytes);
            uploadChunk(fileName, seq, upLoadBytes.length, upLoadBytes, hash);
            upLoadBytes = ConvertUtil.file2Byte(input, CHUNK_SIZE);
            seq++;
        }
    }
}
