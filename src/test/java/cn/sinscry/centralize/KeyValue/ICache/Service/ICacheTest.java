package cn.sinscry.centralize.KeyValue.ICache.Service;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ICacheTest {
    @Test
    public void baseTest() throws InterruptedException {
        ICache<String, String> cache = genCache();
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

    private ICache<String, String> genCache(){
        ICache<String, String> cache = new ICache<>();
        cache.put("1", "1",100);
        cache.put("2", "2",200);
        return cache;
    }

    @Test
    public void persistTest() throws IOException {
        String path="src/test/java/cn/sinscry/centralize/KeyValue/ICache/Service/test.txt";
        ICache<String, String> cache = genCache();
        Assert.assertTrue(cache.persist(path));
        Assert.assertTrue(new File(path).delete());
    }
}
