package cn.sinscry.centralize.KeyValue.ICache.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ICache<K,V> extends ConcurrentHashMap<K,V> {
    // cleaning size limit for each period
    private static final int MAX_EXPIRE_LIMIT = 100;
    // scheduler
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    private static final long GC_period = 200;

    // Expired Map for cleaning outdated data in order
    private final Map<Long, Set<K>> expireTimeSortMap = new TreeMap<>(new Comparator<Long>() {
        @Override
        public int compare(Long o1, Long o2){
            return (int)(o1-o2);
        }
    });
    // Expired Map for removing key from the Set it resided.
    private final Map<K,Set<K>> expireKeyMap = Maps.newHashMap();

    public ICache(){
        super();
        // remove expired key periodically
        EXECUTOR_SERVICE.scheduleAtFixedRate(new ExpireThread(), 0, GC_period, TimeUnit.MILLISECONDS);
    }

    public void put(K key, V value, long expireAtRelative){
        super.put(key, value);
        this.expire(key, expireAtRelative);
    }

    @Override
    public V get(Object key){
        // refresh the value before get
        this.expireKey();
        return super.get(key);
    }

    public void expire(K key, long expireAtRelative){
        long expireAtAbsolute = expireAtRelative + System.currentTimeMillis();
        // put the expiration into the queue
        Set<K> keys = Optional.ofNullable(expireTimeSortMap.get(expireAtAbsolute)).orElse(Sets.newHashSet());
        keys.add(key);
        expireTimeSortMap.put(expireAtAbsolute, keys);
        // remove the key from other time set.
        if(expireKeyMap.containsKey(key)){
            expireKeyMap.get(key).remove(key);
        }
        expireKeyMap.put(key,keys);
    }

    private class ExpireThread implements Runnable{
        @Override
        public void run(){
            if(expireTimeSortMap.isEmpty()){
                return;
            }
            expireKey();
        }
    }

    private synchronized void expireKey(){
        int count = MAX_EXPIRE_LIMIT;
        for(Entry<Long,Set<K>> entry:Lists.newArrayList(expireTimeSortMap.entrySet())){
            if(entry.getValue().isEmpty()){
                expireTimeSortMap.remove(entry.getKey());
                continue;
            }
            int newCount=this.expireKey(entry, count);
            if(newCount==0 || newCount==count){
                return;
            }
            count = newCount;
        }
    }

    // Returns the number of deletions still needed
    private int expireKey(Entry<Long, Set<K>> entry, int count){
        if(count==0 || System.currentTimeMillis()<entry.getKey()){
            return count;
        }
        List<K> keys = Lists.newArrayList(entry.getValue());
        int remainCount = Math.max(count-keys.size(),0);
        while(count--!=remainCount){
            K key = keys.removeFirst();
            entry.getValue().remove(key);
            expireKeyMap.remove(key);
            this.remove(key);
        }
        return ++count;
    }

    public boolean persist(ICache<K, V> cache, String path) throws IOException {
        Set<Map.Entry<K,V>> entrySet = cache.entrySet();
        File file = new File(path);
        if(!file.createNewFile()){
            return false;
        }
        return true;
    }
}
