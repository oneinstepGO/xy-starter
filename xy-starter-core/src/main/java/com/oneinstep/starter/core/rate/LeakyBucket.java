package com.oneinstep.starter.core.rate;

import java.util.concurrent.TimeUnit;

public class LeakyBucket {
    private final int capacity; // 漏桶的容量
    private final int leakRate; // 漏桶的漏水速率（每秒）
    private int water; // 当前水量
    private long lastLeakTime; // 上次漏水的时间

    public LeakyBucket(int capacity, int leakRate) {
        this.capacity = capacity;
        this.leakRate = leakRate;
        this.water = 0;
        this.lastLeakTime = System.nanoTime();
    }

    // 尝试添加请求
    public synchronized boolean allowRequest(int amount) {
        leak(); // 先漏水
        if (water + amount > capacity) {
            // 超过容量，请求被拒绝.
            return false;
        } else {
            // 接受请求
            water += amount;
            return true;
        }
    }

    // 漏水操作
    private void leak() {
        long now = System.nanoTime();
        long elapsedTime = now - lastLeakTime;

        // 计算漏掉的水量
        int leakedWater = (int) (elapsedTime / TimeUnit.SECONDS.toNanos(1) * leakRate);
        if (leakedWater > 0) {
            water = Math.max(0, water - leakedWater);
            lastLeakTime = now;
        }
    }

    public static void main(String[] args) {
        LeakyBucket leakyBucket = new LeakyBucket(10, 2); // 容量为10，每秒漏2单位水

        // 模拟请求
        for (int i = 0; i < 100000; i++) {
            if (leakyBucket.allowRequest(1)) {
                System.out.println("请求 " + i + " 被接受");
            } else {
                System.out.println("请求 " + i + " 被拒绝");
            }

            try {
                Thread.sleep(100); // 每0.5秒发一个请求
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
