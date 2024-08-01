package cn.sinscry.common.pojo;

public class PersistEntry<K,V> {
    K key;
    V value;
    Long expireTime;

    public void setKey(K key){
        this.key = key;
    }
    public K getKey(){
        return this.key;
    }

    public void setValue(V value){
        this.value = value;
    }
    public V getValue(){
        return this.value;
    }

    public void setExpireTime(Long expireTime){
        this.expireTime = expireTime;
    }
    public Long getExpireTime(){
        return this.expireTime;
    }
}
