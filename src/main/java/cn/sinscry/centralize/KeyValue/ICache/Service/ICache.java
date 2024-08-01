package cn.sinscry.centralize.KeyValue.ICache.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ICache<K,V> extends ConcurrentHashMap<K,V> {

    // Only keep the latest expire command
    private final Map<K,Set<K>> expireMap = Maps.newHashMap();
    // Expired Map for cleaning outdated data in order
    private final Map<Long, Set<K>> expireSortMap = new TreeMap<>(new Comparator<Long>() {
        @Override
        public int compare(Long o1, Long o2){
            return (int)(o1-o2);
        }
    });

    // cleaning size limit for each period
    private static final int LIMIT = 100;
    // scheduler
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    public ICache(){
        super();
        // remove expired key periodically
        EXECUTOR_SERVICE.scheduleAtFixedRate(new ExpireThread(), 0, 200, TimeUnit.MILLISECONDS);
    }

    public void expire(K key, long expireAtRelative){
        long expireAtAbsolute = expireAtRelative + System.currentTimeMillis();
        // put the expiration into the queue
        Set<K> keys = expireSortMap.get(expireAtAbsolute);
        if(Objects.isNull(keys)){
            keys = Sets.newHashSet();
        }
        keys.add(key);
        expireSortMap.put(expireAtAbsolute, keys);
        // keep the up-to-date expiration command only
        if(expireMap.containsKey(key)){
            expireMap.get(key).remove(key);
        }
        expireMap.put(key,keys);
    }

    private class ExpireThread implements Runnable{
        @Override
        public void run(){
            if(expireSortMap.isEmpty()){
                return;
            }
            expireKey();
        }
    }

    private synchronized void expireKey(){
        int count = LIMIT;
        int remainCount;
        System.out.println(expireSortMap.entrySet());
        for(Entry<Long,Set<K>> entry:expireSortMap.entrySet()){
            if(count<=0){
                return;
            }
            remainCount=this.expireKey(entry, count);
            if(remainCount==count){
                return;
            }
            count = remainCount;
        }
    }

    // Returns the number of deletions still needed
    private int expireKey(Entry<Long, Set<K>> entry, int count){
        Long expireTime = entry.getKey();
        if(System.currentTimeMillis()<expireTime){
            return count;
        }
        entry.getValue().retainAll(expireMap.keySet());
        List<K> keys = Lists.newArrayList(entry.getValue());
        int res = Math.max(count-keys.size(),0);
        while(count!=res){
            K key = keys.removeFirst();
            entry.getValue().remove(key);
            this.remove(key);
            expireMap.remove(key);
            count--;
        }
        return res;
    }

    // refresh the value before get
    @Override
    public V get(Object key){
        // expire key first;
        this.expireKey();
        return super.get(key);
    }
}
