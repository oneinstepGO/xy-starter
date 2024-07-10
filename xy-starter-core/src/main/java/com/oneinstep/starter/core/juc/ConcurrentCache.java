package com.oneinstep.starter.core.juc;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * 支持并发的缓存
 *
 * @param <K> 缓存计算参数
 * @param <V> 缓存计算结果
 */
@Slf4j
public class ConcurrentCache<K, V> {

    public enum EvictionPolicy {
        /**
         * 最近最少使用
         */
        LRU,
        /**
         * 先进先出
         */
        FIFO,
        /**
         * 最少访问
         */
        LFU,
        /**
         * 无淘汰策略
         */
        NONE
    }

    /**
     * 缓存 LRU、FIFO 使用 LinkedHashMap，其它 使用 ConcurrentHashMap
     */
    private final Map<K, Future<V>> cache;
    /**
     * 最大缓存容量
     */
    @Getter
    private final int maxCapacity;
    /**
     * 淘汰策略
     */
    @Getter
    private final EvictionPolicy policy;
    private final Function<? super K, ? extends V> computeFunction;
    /**
     * 访问次数统计，仅用于 LFU 策略
     */
    private final Map<K, Integer> accessCount;
    /**
     * 锁
     */
    private final Map<K, Lock> locks;
    /**
     * 全局锁，用于 LFU 淘汰策略
     */
    private final Lock globalLock;

    public ConcurrentCache(Function<? super K, ? extends V> computeFunction, int initialCapacity, int maxCapacity, EvictionPolicy policy) {
        this.computeFunction = computeFunction;
        this.maxCapacity = maxCapacity;
        this.policy = policy;
        this.accessCount = (policy == EvictionPolicy.LFU) ? new ConcurrentHashMap<>() : null;
        this.locks = (policy == EvictionPolicy.LRU || policy == EvictionPolicy.FIFO) ? new ConcurrentHashMap<>() : null;
        this.globalLock = (policy == EvictionPolicy.LFU) ? new ReentrantLock() : null;

        if (policy == EvictionPolicy.LRU) {
            this.cache = new LinkedHashMap<>(initialCapacity, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<K, Future<V>> eldest) {
                    return size() > ConcurrentCache.this.maxCapacity;
                }
            };
        } else if (policy == EvictionPolicy.FIFO) {
            this.cache = new LinkedHashMap<>(initialCapacity, 0.75f, false) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<K, Future<V>> eldest) {
                    return size() > ConcurrentCache.this.maxCapacity;
                }
            };
        } else {
            this.cache = new ConcurrentHashMap<>(initialCapacity);
        }
    }

    /**
     * 获取缓存大小
     *
     * @return 缓存大小
     */
    public int getCacheSize() {
        return cache.size();
    }

    public V get(K arg) throws InterruptedException {
        // only get the value if it is already in the cache, don't compute it
        Future<V> f = cache.get(arg);
        if (f == null) {
            return null;
        }
        try {
            return f.get();
        } catch (CancellationException | ExecutionException e) {
            handleCacheException(arg, f, e);
            return null;
        }
    }

    public V getOrCompute(K arg) throws InterruptedException {
        if (locks != null) {
            Lock lock = locks.computeIfAbsent(arg, k -> new ReentrantLock());
            lock.lock();
            try {
                return doCompute(arg);
            } finally {
                lock.unlock();
            }
        } else {
            return doCompute(arg);
        }
    }

    /**
     * 计算缓存
     *
     * @param arg 计算参数
     * @return 计算结果
     * @throws InterruptedException 中断异常
     */
    private V doCompute(K arg) throws InterruptedException {
        while (true) {
            Future<V> f = cache.get(arg);
            if (f == null) {
                log.info("Thread {} can't find the cache. arg: {}", Thread.currentThread().getName(), arg);
                Callable<V> eval = () -> computeFunction.apply(arg);
                FutureTask<V> ft = new FutureTask<>(eval);
                f = cache.putIfAbsent(arg, ft);
                if (f == null) {
                    log.info("Thread {} compute the cache without lock for arg: {}", Thread.currentThread().getName(), arg);
                    f = ft;
                    ft.run();
                }
            }
            if (policy == EvictionPolicy.LFU) {
                handleAccessCount(arg);
            }
            try {
                log.info("Thread {} get the cache of arg: {}", Thread.currentThread().getName(), arg);
                return f.get();
            } catch (CancellationException | ExecutionException e) {
                handleCacheException(arg, f, e);
            }
        }
    }

    /**
     * 处理访问次数
     *
     * @param arg 计算参数
     */
    private void handleAccessCount(K arg) {
        if (accessCount != null) {
            accessCount.put(arg, accessCount.getOrDefault(arg, 0) + 1);
            if (cache.size() > maxCapacity) {
                evictLFUEntry();
            }
        }
    }

    /**
     * 处理缓存异常
     *
     * @param arg 缓存参数
     * @param f   缓存结果
     * @param e   异常
     * @throws InterruptedException 中断异常
     */
    private void handleCacheException(K arg, Future<V> f, Exception e) throws InterruptedException {
        if (e instanceof CancellationException) {
            log.error("Thread {} CancellationException during get the cache of arg: {}", Thread.currentThread().getName(), arg, e);
        } else {
            log.warn("Thread {} ExecutionException during get the cache of arg: {}", Thread.currentThread().getName(), arg, e);
            throw launderThrowable(e.getCause());
        }
        if (locks != null) {
            Lock lock = locks.get(arg);
            if (lock != null) {
                lock.lock();
                try {
                    cache.remove(arg, f);
                } finally {
                    lock.unlock();
                }
            } else {
                cache.remove(arg, f);
            }
        } else {
            cache.remove(arg, f);
        }
    }

    /**
     * 淘汰 LFU 策略的缓存
     */
    private void evictLFUEntry() {
        globalLock.lock();
        try {
            K lfuKey = null;
            int minAccessCount = Integer.MAX_VALUE;
            for (Map.Entry<K, Integer> entry : accessCount.entrySet()) {
                if (entry.getValue() < minAccessCount) {
                    minAccessCount = entry.getValue();
                    lfuKey = entry.getKey();
                }
            }
            if (lfuKey != null) {
                cache.remove(lfuKey);
                accessCount.remove(lfuKey);
            }
        } finally {
            globalLock.unlock();
        }
    }

    /**
     * 处理异常
     *
     * @param cause 异常
     * @return 运行时异常
     */
    private RuntimeException launderThrowable(Throwable cause) {
        if (cause instanceof RuntimeException runtimeException) {
            log.error("Thread {} RuntimeException", Thread.currentThread().getName(), cause);
            return runtimeException;
        } else if (cause instanceof Error error) {
            log.error("Thread {} Error", Thread.currentThread().getName(), cause);
            throw error;
        } else {
            log.error("Thread {} Not unchecked", Thread.currentThread().getName(), cause);
            throw new IllegalStateException("Not unchecked", cause);
        }
    }

//
//    public static void main(String[] args) throws InterruptedException {
//        ConcurrentCache<String, String> concurrentCache = new ConcurrentCache<>(arg -> {
//            log.info("Thread {} is computing the result.", Thread.currentThread().getName());
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                log.warn("Thread {} InterruptedException", Thread.currentThread().getName(), e);
//                Thread.currentThread().interrupt();
//            }
//            log.info("Thread {} finished computing the result.", Thread.currentThread().getName());
//            return arg + " computed";
//        }, 10, 100, EvictionPolicy.LRU);
//
//        // 创建线程
//        Thread[] threads = new Thread[20];
//        ThreadLocalRandom random = ThreadLocalRandom.current();
//        for (int i = 0; i < 20; i++) {
//            Thread t = new Thread(() -> {
//                try {
//                    String value = concurrentCache.compute("key");
//                    log.info("Thread {} get value: {}", Thread.currentThread().getName(), value);
//                } catch (InterruptedException e) {
//                    log.warn("Thread {} InterruptedException", Thread.currentThread().getName(), e);
//                    Thread.currentThread().interrupt();
//                }
//            });
//            threads[i] = t;
//        }
//
//        // 启动线程
//        for (int i = 0; i < 20; i++) {
//            threads[i].start();
//        }
//
//        // 等待线程结束
//        try {
//            for (int i = 0; i < 20; i++) {
//                threads[i].join();
//            }
//        } catch (InterruptedException e) {
//            log.warn("Thread {} InterruptedException", Thread.currentThread().getName(), e);
//            Thread.currentThread().interrupt();
//        }
//
//        log.info("finished");
//    }
}
