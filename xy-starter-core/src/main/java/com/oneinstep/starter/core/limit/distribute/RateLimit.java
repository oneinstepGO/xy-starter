package com.oneinstep.starter.core.limit.distribute;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    String key() default ""; // 限流的key

    int value(); // 每秒允许的最大访问次数

    Algorithm algorithm() default Algorithm.TOKEN_BUCKET; // 限流类型，1-令牌桶，2-漏桶

    RateScope scope() default RateScope.CLUSTER; // 限流范围，集群，单节点

    enum Algorithm {
        // 令牌桶
        TOKEN_BUCKET,
        // 漏桶
        LEAKY_BUCKET;
    }

    enum RateScope {
        // 集群
        CLUSTER,
        // 单节点
        NODE,
    }
}