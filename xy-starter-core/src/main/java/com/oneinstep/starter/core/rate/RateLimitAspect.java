package com.oneinstep.starter.core.rate;

import com.oneinstep.starter.core.error.BaseCodeAndMsgError;
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

    @Before("@annotation(distributeRateLimit)")
    public void checkRateLimit(JoinPoint joinPoint, DistributeRateLimit distributeRateLimit) {
        String methodName = joinPoint.getSignature().getName();
        log.info("请求频率限制，methodName:{}, rateLimit:{}", methodName, distributeRateLimit.value());
        String remoteIpAddress = IPUtil.getRemoteIpAddress(request);
        String ipAddress = StringUtils.isBlank(remoteIpAddress) ? "UNKNOWN" : remoteIpAddress;
        if (StringUtils.isNotBlank(distributeRateLimit.key())) {
            methodName = distributeRateLimit.key();
        }
        String key = "rate_limit:" + ipAddress + ":" + methodName;

        int algorithm = distributeRateLimit.algorithm();
        boolean acquire;
        if (algorithm == 1) {
            // 令牌桶
            RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
            rateLimiter.trySetRate(RateType.PER_CLIENT, distributeRateLimit.value(), 1, RateIntervalUnit.SECONDS);
            acquire = rateLimiter.tryAcquire();
        } else {
            // 漏桶
            DistributedLeakyBucket distributedLeakyBucket = new DistributedLeakyBucket(distributeRateLimit.value(), 1, key, redissonClient);
            acquire = distributedLeakyBucket.allowRequest(1);
        }

        if (!acquire) {
            log.warn("请求频率过高，拒绝请求，key:{}", key);
            throw BaseCodeAndMsgError.REQUEST_FREQUENCY_TOO_HIGH.toOneBaseException();
        }

        log.info("请求频率限制通过，key:{}", key);

    }


}
