package com.oneinstep.starter.core.utils;

/**
 * Twitter的分布式自增ID雪花算法snowflake (Java版)
 */
public class SnowflakeGenerator {
    private final long epoch;
    private final long workerIdBits;
    private final long datacenterIdBits;
    private final long maxWorkerId;
    private final long maxDatacenterId;
    private final long sequenceBits;
    private final long workerIdShift;
    private final long datacenterIdShift;
    private final long timestampLeftShift;
    private final long sequenceMask;

    private final long workerId;
    private final long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    private SnowflakeGenerator(long epoch, long workerIdBits, long datacenterIdBits, long sequenceBits) {
        this.epoch = epoch;
        this.workerIdBits = workerIdBits;
        this.datacenterIdBits = datacenterIdBits;
        this.sequenceBits = sequenceBits;
        this.maxWorkerId = ~(-1L << workerIdBits);
        this.maxDatacenterId = ~(-1L << datacenterIdBits);
        this.workerIdShift = sequenceBits;
        this.datacenterIdShift = sequenceBits + workerIdBits;
        this.timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
        this.sequenceMask = ~(-1L << sequenceBits);

        long workerId = Long.parseLong(System.getProperty("xy.starter.workerId", "1"));
        long datacenterId = Long.parseLong(System.getProperty("xy.starter.datacenterId", "1"));


        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("Worker ID must be between 0 and %d", maxWorkerId));
        }

        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("Datacenter ID must be between 0 and %d", maxDatacenterId));
        }

        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public synchronized long generateId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate ID");
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - epoch) << timestampLeftShift) |
                (datacenterId << datacenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    // 使用静态内部类实现单例
    private static class SingletonHolder {
        private static final SnowflakeGenerator INSTANCE = new SnowflakeGenerator(1695296059701L, 5, 5, 12);
    }

    public static SnowflakeGenerator getInstance() {
        return SingletonHolder.INSTANCE;
    }

}
