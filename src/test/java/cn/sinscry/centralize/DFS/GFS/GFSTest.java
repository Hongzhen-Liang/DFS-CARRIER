package cn.sinscry.centralize.DFS.GFS;

import cn.sinscry.centralize.DFS.GFS.api.MasterApi;
import cn.sinscry.centralize.DFS.GFS.client.ClientBase;
import cn.sinscry.centralize.DFS.GFS.config.SMRMISocket;
import cn.sinscry.centralize.DFS.GFS.master.MasterBase;
import cn.sinscry.centralize.DFS.GFS.worker.WorkerBase;
import cn.sinscry.common.utils.ConfigUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RMISocketFactory;

public class GFSTest {
    private  static final String prefixPath="src/test/java/cn/sinscry/centralize/DFS/GFS/";
    private static final String testFile="test.txt";


    public static void main(String[] args) throws Exception {
        genFile(prefixPath+testFile);
        startMaster(ConfigUtils.MASTER_PORT);
        startWorker(ConfigUtils.MASTER_PORT, ConfigUtils.WORKER_SERVICE_PORT);
        startWorker(ConfigUtils.MASTER_PORT, ConfigUtils.WORKER_SERVICE_PORT+1);
    }

    public static void genFile(String filePath) throws FileNotFoundException {
        PrintStream ps = new PrintStream( new FileOutputStream(filePath));
        ps.println("GFS testing");
    }

    @Test
    public void genFileTest() throws FileNotFoundException {
        genFile(prefixPath+testFile);
    }

    @Test
    public void startWorkerTest() throws Exception {
        startMaster(ConfigUtils.MASTER_PORT);
        startWorker(ConfigUtils.MASTER_PORT, ConfigUtils.WORKER_SERVICE_PORT);
    }

    public static void startMaster(int port) throws IOException {
        String masterIp = InetAddress.getLocalHost().getHostAddress();
        RMISocketFactory.setSocketFactory(new SMRMISocket());
        LocateRegistry.createRegistry(port);
        MasterBase masterBase = new MasterBase();
        Naming.rebind("rmi://"+masterIp+":"+port+"/master", masterBase);
        System.out.println("Master IP Address: " + masterIp + ":"+port);
        System.out.println("Master started");
    }

    public static void startWorker(int masterPort, int workerPort) throws Exception {
        new File(prefixPath+"worker"+workerPort).mkdir();
        String masterIp = InetAddress.getLocalHost().getHostAddress();
        WorkerBase workerBase = new WorkerBase(masterIp, masterPort, workerPort, prefixPath+"/worker"+workerPort+"/");
        LocateRegistry.createRegistry(workerPort);
        Naming.bind("rmi://" + workerBase.getCurrentIpAddr()
                + ":" + workerPort + "/worker", workerBase);
        System.out.println("Worker IP Address: " + workerBase.getCurrentIpAddr()+":"+workerPort);
        System.out.println("Worker Started...");
    }

    @Test
    public void uploadFileTest() throws Exception {
        ClientBase client = startClient(ConfigUtils.MASTER_PORT);
        client.upLoadFile(prefixPath+testFile);
    }

    @Test
    public void downloadFileTest() throws Exception{
        ClientBase client = startClient(ConfigUtils.MASTER_PORT);
        client.downloadFile(testFile);
    }

    @Test
    public void deleteFileTest() throws Exception{
        ClientBase client = startClient(ConfigUtils.MASTER_PORT);
        client.deleteFile(testFile);
    }

    @Test
    public void getFileListTest() throws Exception{
        ClientBase client = startClient(ConfigUtils.MASTER_PORT);
        client.getFileList();
    }


    public ClientBase startClient(int masterPort) throws Exception {
        String masterIp = InetAddress.getLocalHost().getHostAddress();
        MasterApi master = (MasterApi) Naming.lookup("rmi://" + masterIp
                + ":" + masterPort + "/master");
        return new ClientBase(master,prefixPath);
    }

}
