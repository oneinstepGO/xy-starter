package com.oneinstep.starter.core.cache;

import com.oneinstep.starter.core.juc.ConcurrentCache;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Slf4j
class ConcurrentCacheTest {

    private ConcurrentCache<String, String> lruCache;
    private ConcurrentCache<String, String> lfuCache;

    @BeforeEach
    public void setUp() {
        lruCache = new ConcurrentCache<>(arg -> {
            try {
                Thread.sleep(100); // Simulate computation delay
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return arg + " computed";
        }, 10, 3, ConcurrentCache.EvictionPolicy.LRU);

        lfuCache = new ConcurrentCache<>(arg -> {
            try {
                Thread.sleep(100); // Simulate computation delay
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return arg + " computed";
        }, 10, 3, ConcurrentCache.EvictionPolicy.LFU);
    }

    @Test
    void testLRUCacheEviction() throws InterruptedException {
        lruCache.getOrCompute("key1");
        lruCache.getOrCompute("key2");
        lruCache.getOrCompute("key3");

        assertEquals(3, lruCache.getCacheSize());

        lruCache.getOrCompute("key4"); // This should evict "key1"

        assertNull(lruCache.get("key1"));
        assertEquals(3, lruCache.getCacheSize());
    }

    @Test
    void testLFUCacheEviction() throws InterruptedException {
        lfuCache.getOrCompute("key1");
        lfuCache.getOrCompute("key2");
        lfuCache.getOrCompute("key3");

        // Access key1 multiple times to increase its frequency
        lfuCache.getOrCompute("key1");
        lfuCache.getOrCompute("key1");

        // Access key2 once to set its frequency higher than key3
        lfuCache.getOrCompute("key2");

        lfuCache.getOrCompute("key4"); // This should evict "key3"

        // One of "key3" should be evicted, not "key1" or "key2"
        assertNull(lfuCache.get("key3"));
        assertEquals("key1 computed", lfuCache.get("key1"));
        assertEquals("key2 computed", lfuCache.get("key2"));
        assertEquals("key4 computed", lfuCache.get("key4"));
        assertEquals(3, lfuCache.getCacheSize());
    }


    @Test
    void testConcurrency() throws InterruptedException {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

        for (int i = 0; i < 20; i++) {
            final String key = "key" + i % 5;
            executor.submit(() -> {
                try {
                    lruCache.getOrCompute(key);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        executor.shutdown();
        boolean b = executor.awaitTermination(1, TimeUnit.MINUTES);
        log.info("All threads finished: {}", b);
        assertEquals(3, lruCache.getCacheSize());
    }
}
