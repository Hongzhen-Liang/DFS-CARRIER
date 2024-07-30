package cn.sinscry.centralize.KeyValue.LRU;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LRUCache<K,V> {

    private static class Node<K,V>{
        K key;
        V value;
        Node<K,V> pre,next;
        Node(K key, V value){
            this.key = key;
            this.value = value;
            pre = null;
            next = null;
        }
    }

    private int size;
    private final int capacity;
    private final Map<K,Node<K,V>> cache=new HashMap<>();;
    Node<K,V> head,tail;

    LRUCache(int capacity){
        head = new Node<>(null,null);
        tail = new Node<>(null,null);
        head.next = tail;
        tail.pre = head;
        this.capacity = capacity;
    }

    public V get(K key){
        if(this.cache.containsKey(key)){
            Node<K,V> node = this.cache.get(key);
            removeNode(node);
            addNode(node);
            return node.value;
        }else{
            return null;
        }
    }

    public void put(K key, V value){
        if(Objects.isNull(get(key))){
            if(this.cache.size()==this.capacity){
                Node<K,V> node = tail.pre;
                removeNode(node);
            }
            addNode(new Node<>(key,value));
        }
    }

    private void removeNode(Node<K,V> node){
        if(this.cache.containsKey(node.key)){
            this.cache.remove(node.key);
            node.next.pre = node.pre;
            node.pre.next = node.next;
        }
    }
    private void addNode(Node<K,V> node){
        this.cache.put(node.key,node);
        node.next = head.next;
        head.next = node;
        node.pre = head;
    }
}
