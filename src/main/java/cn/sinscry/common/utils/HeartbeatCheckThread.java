package cn.sinscry.common.utils;

import cn.sinscry.centralize.DFS.GFS.master.MasterBase;

public class HeartbeatCheckThread extends Thread{
    private final MasterBase masterBase;
    public HeartbeatCheckThread(MasterBase masterBase){
        this.masterBase = masterBase;
    }

    @Override
    public void run(){
        while(true){
            try {
                Thread.sleep(3000);
                masterBase.heartbeatScan();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
