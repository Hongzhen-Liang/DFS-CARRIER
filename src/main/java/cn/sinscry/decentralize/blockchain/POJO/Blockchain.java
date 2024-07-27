package cn.sinscry.decentralize.blockchain.POJO;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

// 区块链
public class Blockchain implements Serializable, Cloneable{

    // 唯一标识号
    private final String uuid;
    // 交易记录
    private final List<Transaction> current_transactions;
    // 区块数组
    private  List<Block> chain;
    public Blockchain(){
        this.uuid = String.valueOf(UUID.randomUUID());
        this.chain = new ArrayList<>();
        this.current_transactions = new ArrayList<>();
        this.chain.add((new Block(0, this.current_transactions, 0, 0)));
    }

    public static Blockchain mine(Blockchain newBlockchain) {
        Block last_block = newBlockchain.chain.get(newBlockchain.chain.size()-1);
        int proof = Blockchain.proof_of_work(last_block.getProof());
        newBlockchain.current_transactions.add(new Transaction("0",newBlockchain.uuid,1));
        newBlockchain.chain.add(new Block(newBlockchain.chain.size(), newBlockchain.current_transactions, proof, last_block.hashCode()));
        return newBlockchain;
    }

    public static int proof_of_work(int last_proof){
        int proof = (int) (Integer.MAX_VALUE*0.5*Math.random());
        while(!valid_proof(last_proof, proof)){
            proof++;
        }
        return proof;
    }
    public static boolean valid_proof(int last_proof, int proof){
        return sha256Hex(String.format("%d%d", last_proof, proof)).startsWith("0");
    }

    // 检验区块链对不对
    public static boolean valid_chain(Blockchain blockchain){
        Block block;
        List<Block> chain = blockchain.getChain();
        Block last_block = chain.get(0);
        int current_index = 1;
        while(current_index<chain.size()){
            block = chain.get(current_index);
            // 检查计算值对不对
            if(!valid_proof(last_block.getProof(), block.getProof())){
                return false;
            }
            // 检查hash值对不对
            if(block.getPrevious_hash() != last_block.hashCode()){
                return false;
            }
            last_block = block;
            current_index++;
        }
        return true;
    }

    public String getUuid(){return this.uuid;}
    public List<Block> getChain(){return this.chain;}
    public void setChain(List<Block> chain){
        this.chain = chain;
    }
    @Override
    public String toString(){
        StringBuilder buffer = new StringBuilder();
        for(Block b:this.chain){
            buffer.append(b.getTransactions()).append("->");
        }
        return buffer.toString();
    }

    @Override
    public Blockchain clone(){
        try {
            Blockchain blockchain = (Blockchain) super.clone();
            blockchain.setChain(blockchain.chain.stream().map(Block::clone).collect(Collectors.toList()));
            return blockchain;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void updateBlockChain(Blockchain newBlockchain){
        this.setChain(newBlockchain.getChain());
        this.current_transactions.clear();
    }
}
