package cn.sinscry.centralize.KeyValue.ICache;

import cn.sinscry.centralize.KeyValue.ICache.Service.ICache;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ICache<String, String> cache = new ICache<>();
        cache.put("1", "1");
        cache.put("2", "2");

        cache.expire("1", 100);
        assert cache.size()==2;
        System.out.println("Before expire: " + cache.keySet());

        TimeUnit.MILLISECONDS.sleep(500);
        assert cache.size()==1;
        System.out.println("After expire: " + cache.keySet());

        System.exit(0);
    }
}
