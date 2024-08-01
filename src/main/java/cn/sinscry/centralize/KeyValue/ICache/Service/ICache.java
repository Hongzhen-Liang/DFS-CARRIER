package cn.sinscry.centralize.KeyValue.ICache.Service;

import cn.sinscry.common.pojo.PersistEntry;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ICache<K,V> extends ConcurrentHashMap<K,V> {
    // cleaning size limit for each period
    private static final int MAX_EXPIRE_LIMIT = 100;
    // scheduler
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

    String persistPath=null;

    public ICache(){
        super();
        // remove expired key periodically
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new ExpireThread(), 0, GC_period, TimeUnit.MILLISECONDS);
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
        expireAbsolute(key, expireAtAbsolute);
    }
    private void expireAbsolute(K key, long expireAtAbsolute){
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

    private class PersistThread implements Runnable{
        @Override
        public void run(){
            if(persistPath!=null){
                try {
                    persist(persistPath);
                } catch (IOException e) {
                    System.out.println("persist error");
                }
            }
        }
    }

    public synchronized boolean persist(String path) throws IOException {
        try(BufferedWriter out = new BufferedWriter(new FileWriter(path))){
            for(Entry<Long, Set<K>> entry:expireTimeSortMap.entrySet()){
                for(K key:entry.getValue()){
                    PersistEntry<K,V> persistEntry = new PersistEntry<>();
                    persistEntry.setKey(key);
                    persistEntry.setValue(get(key));
                    persistEntry.setExpireTime(entry.getKey());
                    String line = JSON.toJSONString(persistEntry)+"\n";
                    out.write(line);
                }
            }
            out.close();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean load(String path) throws FileNotFoundException {
        try(BufferedReader in = new BufferedReader(new FileReader(path))){
            String line = in.readLine();
            while(line!=null && !line.isEmpty()){
                PersistEntry<K,V> entry = JSON.parseObject(line, PersistEntry.class);
                put(entry.getKey(),entry.getValue());
                expireAbsolute(entry.getKey(),entry.getExpireTime());
                line = in.readLine();
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
