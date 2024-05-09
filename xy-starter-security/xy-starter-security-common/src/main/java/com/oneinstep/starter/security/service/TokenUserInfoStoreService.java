package com.oneinstep.starter.security.service;

import com.oneinstep.starter.security.bean.bo.TokenUserInfoBO;
import com.oneinstep.starter.security.constant.SystemType;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static com.oneinstep.starter.core.error.SecurityCodeAndMsgError.AUTH_INFO_IS_EXPIRED;

/**
 * token用户信息存储服务
 */
@Component
@Slf4j
public class TokenUserInfoStoreService {

    @Resource
    private RedissonClient redissonClient;

    private static final String ACCOUNT_INFO_PREFIX = "session:%s:%d";

    public void storeUserInfo(@NotNull SystemType systemType, @NonNull Long userId, @Valid @NotNull TokenUserInfoBO tokenUserInfoBO) {
        RBucket<TokenUserInfoBO> bucket = redissonClient.getBucket(String.format(ACCOUNT_INFO_PREFIX, systemType.name(), userId));
        bucket.set(tokenUserInfoBO);
        bucket.expire(Duration.of(1, TimeUnit.DAYS.toChronoUnit()));
    }

    public void deleteUserInfo(@NotNull SystemType systemType, @NonNull Long userId) {
        RBucket<TokenUserInfoBO> bucket = redissonClient.getBucket(String.format(ACCOUNT_INFO_PREFIX, systemType.name(), userId));
        bucket.delete();
    }

    public TokenUserInfoBO getUserInfo(@NotNull SystemType systemType, @NonNull Long userId) {
        RBucket<TokenUserInfoBO> bucket = redissonClient.getBucket(String.format(ACCOUNT_INFO_PREFIX, systemType.name(), userId));
        if (!bucket.isExists()) {
            throw AUTH_INFO_IS_EXPIRED.toOneBaseException();
        }
        return bucket.get();
    }

}
