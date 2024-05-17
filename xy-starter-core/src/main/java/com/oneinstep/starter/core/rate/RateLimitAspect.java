package com.oneinstep.starter.core.rate;

import com.oneinstep.starter.core.error.BaseCodeAndMsgError;
import com.oneinstep.starter.core.utils.IPUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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

    @Before("@annotation(rateLimit)")
    public void checkRateLimit(JoinPoint joinPoint, RateLimit rateLimit) {
        String methodName = joinPoint.getSignature().getName();
        log.info("请求频率限制，methodName:{}, rateLimit:{}", methodName, rateLimit.value());
        String ipAddress = IPUtil.getRemoteIpAddress(request);
        String key = "rate_limit:" + ipAddress + ":" + methodName;

        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.PER_CLIENT, rateLimit.value(), 1, RateIntervalUnit.SECONDS);

        if (!rateLimiter.tryAcquire()) {
            log.warn("请求频率过高，拒绝请求，key:{}", key);
            throw BaseCodeAndMsgError.REQUEST_FREQUENCY_TOO_HIGH.toOneBaseException();
        }

    }


}
