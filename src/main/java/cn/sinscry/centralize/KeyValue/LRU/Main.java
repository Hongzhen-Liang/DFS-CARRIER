package cn.sinscry.centralize.KeyValue.LRU;

import cn.sinscry.centralize.KeyValue.LRU.Service.LRUCache;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        LRUCache<Integer, Integer> cache = new LRUCache<>(3);
        cache.put(1,1);
        cache.put(2,2);
        cache.put(3,3);
        assert cache.toString().equals("head->(3,3)->(2,2)->(1,1)->tail");
        cache.put(1,3);
        assert cache.toString().equals("head->(1,3)->(3,3)->(2,2)->tail");
        cache.put(4,4);
        assert cache.toString().equals("head->(4,4)->(1,3)->(3,3)->tail");
        System.out.println(cache);
    }
}
