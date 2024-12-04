package com.oneinstep.starter.security.service;

import com.oneinstep.starter.common.cache.CacheConstants;
import com.oneinstep.starter.security.constant.SystemType;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * JWT 存储
 */
@Component
@Slf4j
public class JwtTokenStore {

    @Resource
    private RedissonClient redissonClient;

    private static final String USER_JWT_PREFIX = "xy:jwt:%s:%d";

    public void storeToken(@NotNull SystemType systemType, @NonNull Long userId, @NotEmpty String jwt) {
        String key = String.format(USER_JWT_PREFIX, systemType.name(), userId);

        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(jwt);
        bucket.expire(Duration.of(1, TimeUnit.DAYS.toChronoUnit()));
    }

    @CacheEvict(cacheNames = CacheConstants.AUTH_JWT, key = "#systemType + ':'+ #userId")
    public void removeToken(@NotNull SystemType systemType, @NonNull Long userId) {
        String key = String.format(USER_JWT_PREFIX, systemType.name(), userId);
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.delete();
    }

    @Cacheable(cacheNames = CacheConstants.AUTH_JWT, key = "#systemType + ':'+ #userId", sync = true)
    public String getJwtToken(@NotNull SystemType systemType, @NonNull Long userId) {
        String key = String.format(USER_JWT_PREFIX, systemType.name(), userId);
        log.info("get token from redis, key: {}", key);
        RBucket<String> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

}
