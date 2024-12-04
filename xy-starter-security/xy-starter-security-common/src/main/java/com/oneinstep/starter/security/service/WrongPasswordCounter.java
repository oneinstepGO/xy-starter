package com.oneinstep.starter.security.service;

import com.oneinstep.starter.security.constant.SystemType;
import com.oneinstep.starter.security.properties.AuthProperties;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 错误密码计数器
 **/
@Slf4j
@Component
public class WrongPasswordCounter {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private AuthProperties authProperties;

    private static final String ACCOUNT_WRONG_PASSWORD_COUNT_PREFIX = "login:wrongPasswordCount:%s:%s";

    public void increaseCount(@NotNull SystemType systemType, @NotEmpty String accountId) {
        RAtomicLong count = redissonClient.getAtomicLong(String.format(ACCOUNT_WRONG_PASSWORD_COUNT_PREFIX, systemType.name(), accountId));
        count.incrementAndGet();
        count.expire(Duration.ofSeconds(authProperties.getWrongPasswordLockTimeSeconds()));
    }

    public boolean isOverTimes(@NotNull SystemType systemType, @NotEmpty String accountId) {
        RAtomicLong count = redissonClient.getAtomicLong(String.format(ACCOUNT_WRONG_PASSWORD_COUNT_PREFIX, systemType.name(), accountId));
        return count.get() >= authProperties.getWrongPasswordTimes();
    }

    public void resetCount(@NotNull SystemType systemType, @NotEmpty String accountId) {
        RAtomicLong count = redissonClient.getAtomicLong(String.format(ACCOUNT_WRONG_PASSWORD_COUNT_PREFIX, systemType.name(), accountId));
        count.delete();
    }

}
