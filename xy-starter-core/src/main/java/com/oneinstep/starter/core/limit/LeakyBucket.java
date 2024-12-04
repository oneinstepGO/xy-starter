package com.oneinstep.starter.core.limit;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class LeakyBucket {
    private final int capacity; // 漏桶的容量
    private final int leakRate; // 漏桶的漏水速率（每秒）
    private final AtomicLong water; // 当前水量
    private final AtomicLong lastLeakTime; // 上次漏水的时间
    private final ReentrantLock lock = new ReentrantLock(); // 锁

    public LeakyBucket(int capacity, int leakRate) {
        this.capacity = capacity;
        this.leakRate = leakRate;
        this.water = new AtomicLong(0);
        this.lastLeakTime = new AtomicLong(System.nanoTime());
    }

    // 尝试添加请求
    public boolean allowRequest(int amount) {
        leak(); // 先漏水
        lock.lock();
        try {
            if (water.get() + amount > capacity) {
                // 超过容量，请求被拒绝
                return false;
            } else {
                // 接受请求
                water.addAndGet(amount);
                return true;
            }
        } finally {
            lock.unlock();
        }
    }

    // 漏水操作
    private void leak() {
        long now = System.nanoTime();
        long elapsedTime = now - lastLeakTime.get();

        // 计算漏掉的水量
        long leakedWater = (elapsedTime / TimeUnit.SECONDS.toNanos(1)) * leakRate;
        if (leakedWater > 0) {
            lock.lock();
            try {
                // 再次检查并更新，以确保线程安全
                if (lastLeakTime.get() < now) {
                    water.updateAndGet(currentWater -> Math.max(0, currentWater - leakedWater));
                    lastLeakTime.set(now);
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        LeakyBucket leakyBucket = new LeakyBucket(10, 10); // 容量为10，每秒漏10单位水

        // 模拟请求
        for (int i = 0; i < 1000000; i++) {
            if (leakyBucket.allowRequest(1)) {
                System.out.println("请求 " + i + " 被接受");
            } else {
                System.out.println("请求 " + i + " 被拒绝");
            }

            try {
                Thread.sleep(10); // 每0.1秒发一个请求
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public int getRate() {
        return this.leakRate;
    }
}
