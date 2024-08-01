package cn.sinscry.centralize.KeyValue.ICache;

import cn.sinscry.centralize.KeyValue.ICache.Service.ICache;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ICache<String, String> cache = new ICache<>();
        cache.put("1", "1",100);
        cache.put("2", "2",200);
        System.out.println("Initial: " + cache.keySet());
        assert cache.keySet().toString().equals("[1, 2]");

        TimeUnit.MILLISECONDS.sleep(150);
        System.out.println("Before get: " + cache.keySet());
        assert cache.keySet().toString().equals("[1, 2]");

        cache.get("3");
        System.out.println("After get: " + cache.keySet());
        assert cache.keySet().toString().equals("[2]");

        TimeUnit.MILLISECONDS.sleep(500);
        System.out.println("After expire: " + cache.keySet());
        assert cache.keySet().toString().equals("[]");

        System.exit(0);
    }
}
