package cn.sinscry.centralize.DFS.GFS;

import cn.sinscry.centralize.DFS.GFS.config.SMRMISocket;
import cn.sinscry.centralize.DFS.GFS.master.MasterNode;
import cn.sinscry.common.utils.ConfigUtils;

import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RMISocketFactory;

public class Main {
    public static void main(String[] args) {
        try {
            String masterIp = InetAddress.getLocalHost().getHostAddress();
            RMISocketFactory.setSocketFactory(new SMRMISocket());
            LocateRegistry.createRegistry(ConfigUtils.MASTER_PORT);
            MasterNode masterNode = new MasterNode();
            Naming.rebind("rmi://"+masterIp+":"+ConfigUtils.MASTER_PORT+"/master", masterNode);
            System.out.println("Master IP Address: " + masterIp);
            System.out.println("Master started");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
