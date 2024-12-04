package com.oneinstep.starter.core.limit.distribute;

import com.oneinstep.starter.core.error.BaseCodeAndMsgError;
import com.oneinstep.starter.core.limit.LeakyBucket;
import com.oneinstep.starter.core.limit.TokenBucket;
import com.oneinstep.starter.core.utils.IPUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

import static com.oneinstep.starter.core.limit.distribute.RateLimit.Algorithm.TOKEN_BUCKET;

/**
 * 限流切面
 */
@Aspect
@Component
@Slf4j
public class RateLimitAspect {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private HttpServletRequest request;
    // 使用 ConcurrentHashMap 存储 TokenBucket 实例
    private final ConcurrentHashMap<String, TokenBucket> tokenBucketMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LeakyBucket> leakyBucketMap = new ConcurrentHashMap<>();


    @Before("@annotation(rateLimit)")
    public void checkRateLimit(JoinPoint joinPoint, RateLimit rateLimit) {
        String methodName = joinPoint.getSignature().getName();

        int rate = rateLimit.value();

        log.info("请求频率限制，methodName:{}, rate:{}", methodName, rate);
        String remoteIpAddress = IPUtil.getRemoteIpAddress(request);
        String ipAddress = StringUtils.isBlank(remoteIpAddress) ? "UNKNOWN" : remoteIpAddress;
        if (StringUtils.isNotBlank(rateLimit.key())) {
            methodName = rateLimit.key();
        }
        String key = "rate_limit:" + ipAddress + ":" + methodName;

        RateLimit.Algorithm algorithm = rateLimit.algorithm();
        RateLimit.RateScope scope = rateLimit.scope();
        boolean acquire;
        // 令牌桶
        if (algorithm == TOKEN_BUCKET) {
            if (scope == RateLimit.RateScope.CLUSTER) {
                RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
                rateLimiter.trySetRate(RateType.PER_CLIENT, rateLimit.value(), 1, RateIntervalUnit.SECONDS);
                acquire = rateLimiter.tryAcquire();
            } else {
                // 根据 key 获取 TokenBucket 实例
                TokenBucket tokenBucket = tokenBucketMap.computeIfAbsent(key, k -> new TokenBucket(rate, rate, 1000));
                // 如果 rate 发生了变化，重新实例化 LeakyBucket
                if (tokenBucket.getRate() != rate) {
                    tokenBucket = new TokenBucket(rate, rate, 1000);
                    tokenBucketMap.put(key, tokenBucket);
                }
                acquire = tokenBucket.tryConsume(1);
            }

        }
        // 漏桶
        else {
            if (scope == RateLimit.RateScope.CLUSTER) {
                DistributedLeakyBucket distributedLeakyBucket = new DistributedLeakyBucket(rateLimit.value(), 1, key, redissonClient);
                acquire = distributedLeakyBucket.allowRequest(1);
            } else {
                LeakyBucket leakyBucket = leakyBucketMap.computeIfAbsent(key, k -> new LeakyBucket(rate, rate));
                // 如果 rate 发生了变化，重新实例化 LeakyBucket
                if (leakyBucket.getRate() != rate) {
                    leakyBucket = new LeakyBucket(rate, rate);
                    leakyBucketMap.put(key, leakyBucket);
                }
                acquire = leakyBucket.allowRequest(1);
            }

        }

        if (!acquire) {
            log.warn("请求频率过高，拒绝请求，key:{}", key);
            throw BaseCodeAndMsgError.REQUEST_FREQUENCY_TOO_HIGH.toOneBaseException();
        }

        log.info("请求频率限制通过，key:{}", key);

    }


}
