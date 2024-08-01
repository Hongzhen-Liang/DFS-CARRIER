package cn.sinscry.centralize.KeyValue.LRU.Service;

import cn.sinscry.common.pojo.LinkedNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LRUCache<K,V> {
    private final int capacity;
    private final Map<K,LinkedNode<K,V>> cache=new HashMap<>();;
    LinkedNode<K,V> head,tail;

    public LRUCache(int capacity){
        head = new LinkedNode<>(null,null);
        tail = new LinkedNode<>(null,null);
        head.setNext(tail);
        tail.setPre(head);
        this.capacity = capacity;
    }

    public V get(K key){
        if(this.cache.containsKey(key)){
            LinkedNode<K,V> node = this.cache.get(key);
            removeNode(node);
            addNode(node);
            return node.getValue();
        }else{
            return null;
        }
    }

    public void put(K key, V value){
        // get will make head point to node
        if(Objects.isNull(get(key))){
            if(this.cache.size()==this.capacity){
                LinkedNode<K,V> node = tail.getPre();
                removeNode(node);
            }
            addNode(new LinkedNode<>(key,value));
        }else{
            // update node's value
            this.cache.get(key).setValue(value);
        }

    }

    private void removeNode(LinkedNode<K,V> node){
        if(this.cache.containsKey(node.getKey())){
            this.cache.remove(node.getKey());
            node.getNext().setPre(node.getPre());
            node.getPre().setNext(node.getNext());
        }
    }
    private void addNode(LinkedNode<K,V> node){
        this.cache.put(node.getKey(),node);
        node.setNext(head.getNext());
        head.setNext(node);
        node.setPre(head);
        node.getNext().setPre(node);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("head->");
        LinkedNode<K,V> cur = head.getNext();
        while(cur!=tail){
            sb.append("(").append(cur.getKey()).append(",").append(cur.getValue()).append(")->");
            cur = cur.getNext();
        }
        sb.append("tail");
        return sb.toString();
    }
}
