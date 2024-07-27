package cn.sinscry.decentralize.blockchain.Service;

import cn.sinscry.decentralize.blockchain.POJO.Blockchain;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server {
    private final int port;
    private final Blockchain blockchain;
    private final List<Integer> neighbours;

    public Server(int port, List<Integer> neighbours){
        this.port = port;
        this.neighbours = neighbours;
        this.blockchain = new Blockchain();
    }
    public void start_server(){
        Thread replier = new Replier();
        replier.start();

        Thread miner = new Miner();
        miner.start();
    }

    class Replier extends Thread {
        @Override
        public void run() {
            try{
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.printf("%s 在端口 %d 监听\n", blockchain.getUuid(), port);
                while (true) {
                    Socket sock = serverSocket.accept();
                    ObjectInputStream ObjIn = new ObjectInputStream(sock.getInputStream());
                    Blockchain neighbourBlockChain = (Blockchain) ObjIn.readObject();
                    if (neighbourBlockChain.getChain().size() > blockchain.getChain().size() && Blockchain.valid_chain(neighbourBlockChain)) {
                        blockchain.updateBlockChain(neighbourBlockChain);
                        System.out.println(blockchain.getUuid()+"完成同步: "+blockchain);
                    }
                    sock.close();
                }
            }catch (Exception e){
                System.out.println(e);
            }
        }
    }
    /** 定时打包 */
    class Miner extends Thread{
        @Override
        public void run(){
            while (true){
                try {
                    Thread.sleep(2000);
                    Blockchain newBlockchain = Blockchain.mine(blockchain.clone());
                    if(newBlockchain.getChain().size()>blockchain.getChain().size()) {
                        blockchain.updateBlockChain(newBlockchain);
                        System.out.println(blockchain.getUuid() + "打包成功\n" + blockchain);
                        for (int node : neighbours) {
                            if (node != port) sendRequestVo(node, newBlockchain);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    /** 同步开始 */
    void sendRequestVo(int port, Blockchain chain) throws IOException, ClassNotFoundException {
        Socket client = new Socket("LocalHost", port);
        ObjectOutputStream ObjOut = new ObjectOutputStream(client.getOutputStream());
        try {
            ObjOut.writeObject(chain);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        finally {
            client.close();
        }
    }
}
