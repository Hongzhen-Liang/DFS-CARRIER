package cn.sinscry.common.pojo;

public class LinkedNode<K,V> {
    private K key;
    private V value;
    private LinkedNode<K,V> pre,next;
    public LinkedNode(K key, V value){
        this.key = key;
        this.value = value;
        pre = null;
        next = null;
    }

    public K getKey() {
        return key;
    }
    public V getValue(){
        return value;
    }
    public void setValue(V value){
        this.value = value;
    }
    public LinkedNode<K,V> getPre(){
        return pre;
    }
    public void setPre(LinkedNode<K,V> pre){
        this.pre = pre;
    }
    public LinkedNode<K,V> getNext(){
        return next;
    }
    public void setNext(LinkedNode<K,V> next){
        this.next=next;
    }

}
