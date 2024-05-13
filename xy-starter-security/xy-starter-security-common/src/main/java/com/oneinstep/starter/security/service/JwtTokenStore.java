package com.oneinstep.starter.security.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.oneinstep.starter.security.constant.SystemType;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
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

    private final Cache<String, String> jwtCache = Caffeine.newBuilder()
            .expireAfterWrite(600, TimeUnit.SECONDS)
            .maximumSize(1000)
            .build();

    private static final String USER_JWT_PREFIX = "xy:jwt:%s:%d";

    public void storeToken(@NotNull SystemType systemType, @NonNull Long userId, @NotEmpty String jwt) {
        String key = String.format(USER_JWT_PREFIX, systemType.name(), userId);

        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(jwt);
        bucket.expire(Duration.of(1, TimeUnit.DAYS.toChronoUnit()));

        jwtCache.put(key, jwt);
    }

    public void removeToken(@NotNull SystemType systemType, @NonNull Long userId) {
        String key = String.format(USER_JWT_PREFIX, systemType.name(), userId);
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.delete();

        jwtCache.invalidate(key);
    }

    public boolean isTokenExistAndRight(@NotNull SystemType systemType, @NonNull Long userId, @NotEmpty String jwt) {
        String key = String.format(USER_JWT_PREFIX, systemType.name(), userId);
        String jwtFromCache = jwtCache.get(key, this::getTokenFromRedis);
        log.info("jwt from cache: {}", jwtFromCache);
        return StringUtils.isNotBlank(jwtFromCache) && jwtFromCache.equals(jwt);
    }

    private String getTokenFromRedis(String key) {
        log.info("get token from redis, key: {}", key);
        RBucket<String> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

}
