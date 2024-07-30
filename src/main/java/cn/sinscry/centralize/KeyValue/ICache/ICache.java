package cn.sinscry.centralize.KeyValue.ICache;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.security.Key;
import java.util.Comparator;
import java.util.HashMap;
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
    // Expired Map for cleaning outdated data
    private final Map<Long, Set<K>> expireSortMap = new TreeMap<>(new Comparator<Long>() {
        @Override
        public int compare(Long o1, Long o2){
            return (int)(o1-o2);
        }
    });
    public void expire(K key, long expireAt){
        Set<K> keys = expireSortMap.get(expireAt);
        if(Objects.isNull(keys)){
            keys = Sets.newHashSet();
        }
        keys.add(key);
        expireSortMap.put(expireAt, keys);
    }

    // cleaning size limit for each period
    private static final int LIMIT = 100;
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    private static final long startTime = System.currentTimeMillis();

    ICache(){
        super();
        // remove expired key periodically
        EXECUTOR_SERVICE.scheduleAtFixedRate(new ExpireThread(), 0, 100, TimeUnit.MILLISECONDS);
    }

    private class ExpireThread implements Runnable{
        @Override
        public void run(){
            if(expireSortMap.isEmpty()){
                return;
            }
            int count = LIMIT;
            for(Entry<Long,Set<K>> entry:expireSortMap.entrySet()){
                if(count<=0){
                    return;
                }
                count=expireKey(entry,count);
            }
        }
        private int expireKey(Entry<Long, Set<K>> entry, int count){
            Long expireTime = entry.getKey();
            if(System.currentTimeMillis()-startTime<expireTime){
                return count;
            }
            entry.getValue().retainAll(keySet());
            List<K> keys = Lists.newArrayList(entry.getValue());

            int res = Math.max(count-keys.size(),0);
            while(count!=res){
                K key = keys.removeFirst();
                entry.getValue().remove(key);
                remove(key);
                count--;
            }
            return res;
        }
    }


}
