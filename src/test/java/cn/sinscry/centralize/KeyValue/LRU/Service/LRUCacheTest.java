package cn.sinscry.centralize.KeyValue.LRU.Service;

import org.junit.Assert;
import org.junit.Test;

public class LRUCacheTest {
    @Test
    public void baseTest(){
        LRUCache<Integer, Integer> cache = new LRUCache<>(3);
        cache.put(1,1);
        cache.put(2,2);
        cache.put(3,3);
        Assert.assertEquals("head->(3,3)->(2,2)->(1,1)->tail", cache.toString());
        cache.put(1,3);
        Assert.assertEquals("head->(1,3)->(3,3)->(2,2)->tail", cache.toString());
        cache.put(4,4);
        Assert.assertEquals("head->(4,4)->(1,3)->(3,3)->tail", cache.toString());
        System.out.println(cache);
    }
}
