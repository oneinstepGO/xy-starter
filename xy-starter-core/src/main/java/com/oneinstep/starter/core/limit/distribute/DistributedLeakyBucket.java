package com.oneinstep.starter.core.limit.distribute;

import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DistributedLeakyBucket {
    private final int capacity;
    private final int leakRate;
    private final String bucketKey; // Redis中的键
    private final RedissonClient redissonClient;

    private static final String WATER = "water";
    private static final String LAST_LEAK_TIME = "lastLeakTime";

    public DistributedLeakyBucket(int capacity, int leakRate, String bucketKey, RedissonClient redissonClient) {
        this.capacity = capacity;
        this.leakRate = leakRate;
        this.bucketKey = bucketKey;
        this.redissonClient = redissonClient;
    }

    // 尝试添加请求
    public boolean allowRequest(int amount) {
        String script = "local water = redis.call('HGET', KEYS[1], ARGV[1]) " +
                "if not water then water = 0 else water = tonumber(water) end " +
                "local lastLeakTime = redis.call('HGET', KEYS[1], ARGV[2]) " +
                "if not lastLeakTime then lastLeakTime = tonumber(ARGV[4]) else lastLeakTime = tonumber(lastLeakTime) end " +
                "local now = tonumber(ARGV[3]) " +
                "if not now then " +
                "   return redis.error_reply('now is nil: ARGV[3] = ' .. tostring(ARGV[3]) .. ', ARGV[1] = ' .. tostring(ARGV[1]) .. ', ARGV[2] = ' .. tostring(ARGV[2]) .. ', ARGV[4] = ' .. tostring(ARGV[4]) .. ', ARGV[5] = ' .. tostring(ARGV[5]) .. ', ARGV[6] = ' .. tostring(ARGV[6]) .. ', ARGV[7] = ' .. tostring(ARGV[7])) " +
                "end " +
                "local elapsedTime = now - lastLeakTime " +
                "local leakRate = tonumber(ARGV[5]) " +
                "local leakedWater = math.floor(elapsedTime / 1000000000 * leakRate) " +
                "if leakedWater > 0 then " +
                "   water = math.max(0, water - leakedWater) " +
                "   redis.call('HSET', KEYS[1], ARGV[1], water) " +
                "   redis.call('HSET', KEYS[1], ARGV[2], now) " +
                "end " +
                "if water + tonumber(ARGV[6]) > tonumber(ARGV[7]) then " +
                "   return 0 " +
                "else " +
                "   water = water + tonumber(ARGV[6]) " +
                "   redis.call('HSET', KEYS[1], ARGV[1], water) " +
                "   return 1 " +
                "end";

        List<Object> keys = Collections.singletonList(bucketKey);

        long currentTime = System.nanoTime();
        List<Object> args = Arrays.asList(
                WATER,
                LAST_LEAK_TIME,
                String.valueOf(currentTime),
                String.valueOf(currentTime),
                String.valueOf(leakRate),
                String.valueOf(amount),
                String.valueOf(capacity)
        );

        RScript scriptExecutor = redissonClient.getScript();
        Long result = scriptExecutor.eval(RScript.Mode.READ_WRITE, script, RScript.ReturnType.INTEGER, keys, args.toArray());
        return result == 1;
    }


}
