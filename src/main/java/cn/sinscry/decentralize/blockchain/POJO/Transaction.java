package cn.sinscry.decentralize.blockchain.POJO;

import java.io.Serializable;

// 交易记录
public class Transaction implements Serializable, Cloneable {
    private final String sender;
    private final String recipient;
    private final int amount;
    Transaction(String sender, String recipient, int amount){
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
    }

    @Override
    public String toString(){
        return this.sender+ "向" + this.recipient + "支付了" + amount + "元";
    }

    @Override
    public Transaction clone(){
        try {
            return (Transaction) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
