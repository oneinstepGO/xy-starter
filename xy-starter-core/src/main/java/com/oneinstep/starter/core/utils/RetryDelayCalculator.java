package com.oneinstep.starter.core.utils;

/**
 * 计算重试延时工具类
 */
public class RetryDelayCalculator {

    // 私有构造器以防止实例化
    private RetryDelayCalculator() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 计算第 i 次重试的相对延时
     *
     * @param retryCount    当前重试次数
     * @param maxRetryCount 最大重试次数
     * @param initDelayMs   初始延时
     * @param maxDelayMs    最大延迟时间
     * @return 第 i 次重试的相对延时
     */
    public static long calculateDelay(int retryCount, int maxRetryCount, long initDelayMs, long maxDelayMs) {
        if (retryCount < 1) {
            throw new IllegalArgumentException("Retry count must be greater than or equal to 1.");
        }
        if (retryCount > maxRetryCount) {
            throw new IllegalArgumentException("Retry count exceeds maximum retry count.");
        }
        long delay = initDelayMs * (1L << (retryCount - 1));
        return Math.min(delay, maxDelayMs);
    }

    public static void main(String[] args) {
        int maxRetryCount = 5;
        long initDelayMs = 1000; // 1 second
        long maxDelayMs = 16000; // 16 seconds

        for (int i = 1; i <= maxRetryCount; i++) {
            long delay = RetryDelayCalculator.calculateDelay(i, maxRetryCount, initDelayMs, maxDelayMs);
            System.out.println("Retry " + i + " delay: " + delay + " ms");
        }
    }
}