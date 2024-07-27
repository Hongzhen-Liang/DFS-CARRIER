package cn.sinscry.decentralize.blockchain.POJO;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;


// 交易块
public class Block implements Serializable, Cloneable {
    private final int index;
    private final Timestamp timestamp;
    private List<Transaction> transactions;
    private final int proof;
    private final int previous_hash;
    Block(int index, List<Transaction> transactions, int proof, int previous_hash){
        this.index = index;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.transactions = transactions.stream().map(Transaction::clone).collect(Collectors.toList());
        this.proof = proof;
        this.previous_hash = previous_hash;
    }

    int getProof(){return this.proof;}
    int getPrevious_hash(){return this.previous_hash;}

    public List<Transaction> getTransactions(){return this.transactions;}
    public void setTransactions(List<Transaction> transactions){
        this.transactions = transactions;
    }

    @Override
    public int hashCode(){
        return sha256Hex(this.index+this.proof+this.previous_hash+this.timestamp.toString()+this.transactions).hashCode();
    }

    @Override
    public Block clone(){
        try {
            Block block = (Block) super.clone();
            block.setTransactions(block.transactions.stream().map(Transaction::clone).collect(Collectors.toList()));
            return block;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}