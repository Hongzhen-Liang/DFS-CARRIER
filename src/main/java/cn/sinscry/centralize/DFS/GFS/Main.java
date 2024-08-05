package cn.sinscry.centralize.DFS.GFS;

import cn.sinscry.centralize.DFS.GFS.config.SMRMISocket;
import cn.sinscry.centralize.DFS.GFS.master.MasterBase;
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
            MasterBase masterBase = new MasterBase();
            Naming.rebind("rmi://"+masterIp+":"+ConfigUtils.MASTER_PORT+"/master", masterBase);
            System.out.println("Master IP Address: " + masterIp);
            System.out.println("Master started");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
