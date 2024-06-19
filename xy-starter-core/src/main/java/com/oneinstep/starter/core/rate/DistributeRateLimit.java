package com.oneinstep.starter.core.rate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributeRateLimit {
    String key() default ""; // 限流的key
    int value(); // 每秒允许的最大访问次数
    int algorithm() default 1; // 限流类型，1-令牌桶，2-漏桶
}