package com.oneinstep.starter.core.limit;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class TokenBucket {
    private final int capacity; // 桶的容量
    private final int refillTokens; // 每次填充的令牌数量
    private final long refillInterval; // 填充间隔时间（毫秒）
    private final AtomicLong tokens; // 当前令牌数量
    private final AtomicLong lastRefillTime; // 上一次填充时间
    private final ReentrantLock lock; // 锁

    public TokenBucket(int capacity, int refillTokens, long refillInterval) {
        this.capacity = capacity;
        this.refillTokens = refillTokens;
        this.refillInterval = refillInterval;
        this.tokens = new AtomicLong(capacity);
        this.lastRefillTime = new AtomicLong(System.currentTimeMillis());
        this.lock = new ReentrantLock();
    }

    // 尝试获取令牌
    public boolean tryConsume(int numTokens) {
        refill();

        if (tokens.get() >= numTokens) {
            tokens.addAndGet(-numTokens);
            return true;
        } else {
            return false;
        }
    }

    // 填充令牌
    private void refill() {
        long now = System.currentTimeMillis();
        long lastTime = lastRefillTime.get();

        if (now > lastTime) {
            long elapsedTime = now - lastTime;
            long newTokens = (elapsedTime / refillInterval) * refillTokens;

            if (newTokens > 0) {
                lock.lock();
                try {
                    if (lastRefillTime.compareAndSet(lastTime, now)) {
                        tokens.set(Math.min(capacity, tokens.get() + newTokens));
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TokenBucket tokenBucket = new TokenBucket(10, 1, 1000);

        Runnable task = () -> {
            for (int i = 0; i < 20; i++) {
                if (tokenBucket.tryConsume(1)) {
                    System.out.println("Request " + i + " allowed.");
                } else {
                    System.out.println("Request " + i + " denied.");
                }
                try {
                    Thread.sleep(500); // 模拟请求间隔
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        // 启动多个线程进行测试
        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
    }

    public int getRate() {
        return refillTokens;
    }
}
