package cn.sinscry.common.utils;

import cn.sinscry.centralize.DFS.GFS.master.MasterNode;

public class HeartbeatUtils {
    public static class HeartbeatCheckThread extends Thread{
        private final MasterNode masterNode;
        public HeartbeatCheckThread(MasterNode masterNode){
            this.masterNode = masterNode;
        }

        @Override
        public void run(){
            while(true){
                try {
                    Thread.sleep(30000);
                    masterNode.heartbeatScan();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

}
