package com.oneinstep.starter.core.redis;

import com.oneinstep.starter.core.exception.FailToGetLockException;
import com.oneinstep.starter.core.utils.SpringBeanUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redisson 工具类
 * 主要提供了在分布式锁中执行 Runnable 或者 Supplier 的能力
 * 如果需要使用 bss-light-tool 自动装配的 RedissonClient 客户端，
 * 需要配置 bss.light.redisson.enable=true
 * 示例配置
 * bss:
 * light:
 * redisson:
 * enable: true
 * nodeAddress: xx
 * masterConnectionPoolSize: 300
 * slaveConnectionPoolSize: 500
 * connectTimeout: 10000
 **/
@UtilityClass
@Slf4j
public class DistributedLockUtil {

    /**
     * 限制可设置的最大的最大锁等待时间
     * 避免长时间阻塞线程
     */
    public static final long MAX_MAX_WAIT_LOCK_TIME_MS = 3000;
    /**
     * 不等待锁，立即返回抢锁失败或者成功
     */
    public static final long NO_WAIT = 0;

    /**
     * 在分布式锁中执行 Runnable
     * 会立即尝试获取锁，不会等待，不会阻塞，获取锁失败将抛出异常 HSTPromptingBaseException("10000010", "Can't get the distributed lock.")
     *
     * @param lockName 锁名
     * @param action   待执行的Runnable
     */
    public static void tryLockAndRun(@Valid @NotNull(message = "lockName can't be null.") String lockName, Runnable action) {
        try {
            tryLockAndSupply(lockName, NO_WAIT, () -> {
                action.run();
                return null;
            });
        } catch (InterruptedException e) {
            // shouldn't be here
            log.error("tryLockAndRun InterruptedException", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 在分布式锁中执行 Runnable
     * 尝试获取锁，最多等待 {waitTime} ms，超过这个时间还没获取到锁，则获取锁失败，将抛出异常 HSTPromptingBaseException("10000010", "Can't get the distributed lock.")
     *
     * @param lockName 锁名
     * @param waitTime 获取锁的最大等待时间，单位：毫秒，超过3000ms 会强行设置为 3000ms
     * @param action   待执行的Runnable
     */
    public static void tryLockAndRun(@Valid @NotNull(message = "lockName can't be null.") String lockName, Long waitTime, Runnable action) throws InterruptedException {
        tryLockAndSupply(lockName, waitTime, () -> {
            action.run();
            return null;
        });
    }

    /**
     * 在分布式锁中执行 Supplier，并返回 Supplier 执行的结果
     * 会立即尝试获取锁，不会等待，不会阻塞，获取锁失败将抛出异常 HSTPromptingBaseException("10000010", "Can't get the distributed lock.")
     *
     * @param lockName 锁名
     * @param supplier 待执行的supplier
     * @param <T>      supplier 返回的数据类型
     * @return supplier 返回的数据，也可能Supplier执行失败，抛出异常，需要自己处理
     */
    public static <T> T tryLockAndSupply(@Valid @NotNull(message = "lockName can't be null.") String lockName, Supplier<T> supplier) {
        try {
            return tryLockAndSupply(lockName, NO_WAIT, supplier);
        } catch (InterruptedException e) {
            // shouldn't be here
            log.error("tryLockAndRun InterruptedException", e);
            Thread.currentThread().interrupt();
        }
        return null;
    }

    /**
     * 在分布式锁中执行 Supplier，并返回 Supplier 执行的结果
     * 尝试获取锁，最多等待 {waitTime} ms，超过这个时间还没获取到锁，则获取锁失败，将抛出异常 HSTPromptingBaseException("10000010", "Can't get the distributed lock.")
     *
     * @param lockName 锁名
     * @param supplier 待执行的supplier
     * @param <T>      supplier 返回的数据类型
     * @param waitTime 获取锁的最大等待时间，单位：毫秒，超过3000ms 会强行设置为 3000ms
     * @return T，也可能Supplier执行失败，抛出异常
     */
    public static <T> T tryLockAndSupply(@Valid @NotNull(message = "lockName can't be null.") String lockName, Long waitTime, Supplier<T> supplier) throws InterruptedException {
        log.info("tryLockAndSupply >>>>>>>>>> lockName={}, waitTime={} ms", lockName, waitTime);
        RedissonClient redissonClient = SpringBeanUtil.getBean(RedissonClient.class);
        RLock lock = redissonClient.getLock(lockName);
        try {
            boolean result;
            // redisson lock 有默认过期时间 30s 和看门狗机制，不会发生死锁
            if (waitTime == null || waitTime <= NO_WAIT) {
                result = lock.tryLock();
            } else {
                if (waitTime > MAX_MAX_WAIT_LOCK_TIME_MS) {
                    waitTime = MAX_MAX_WAIT_LOCK_TIME_MS;
                }
                result = lock.tryLock(waitTime, TimeUnit.MILLISECONDS);
            }
            if (!result) {
                log.warn("Can't get the lock. lockName={}", lockName);
                throw new FailToGetLockException();
            }
            log.info("Get the lock success.  lockName={}", lockName);
            return supplier.get();
        } catch (InterruptedException e) {
            log.error("get lock InterruptedException error", e);
            throw e;
        } finally {
            log.info("Do finally in distributed lock. lockName={}, isLocked={}, isHeldByCurrentThread={}",
                    lockName, lock.isLocked(), lock.isHeldByCurrentThread());
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("Release the lock success.  lockName={}", lockName);
            }
        }
    }

}
