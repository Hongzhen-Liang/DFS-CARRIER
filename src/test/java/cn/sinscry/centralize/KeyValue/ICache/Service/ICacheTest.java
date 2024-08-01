package cn.sinscry.centralize.KeyValue.ICache.Service;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class ICacheTest {
    @Test
    public void baseTest() throws InterruptedException {
        ICache<String, String> cache = new ICache<>();
        cache.put("1", "1",100);
        cache.put("2", "2",200);
        System.out.println("Initial: " + cache.keySet());
        Assert.assertEquals("[1, 2]", cache.keySet().toString());

        TimeUnit.MILLISECONDS.sleep(150);
        System.out.println("Before get: " + cache.keySet());
        Assert.assertEquals("[1, 2]",cache.keySet().toString());

        cache.get("3");
        System.out.println("After get: " + cache.keySet());
        Assert.assertEquals("[2]", cache.keySet().toString());

        TimeUnit.MILLISECONDS.sleep(500);
        System.out.println("After expire: " + cache.keySet());
        Assert.assertEquals("[]",cache.keySet().toString());
    }
}
