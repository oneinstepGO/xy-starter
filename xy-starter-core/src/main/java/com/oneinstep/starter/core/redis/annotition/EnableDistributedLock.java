package com.oneinstep.starter.core.redis.annotition;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableDistributedLock {
    /**
     * 锁的前缀
     *
     * @return 锁的前缀
     */
    String lockPrefix() default "";

    /**
     * 尝试获取锁的时间
     *
     * @return 尝试获取锁的时间
     */
    long lockTimeout() default 0;
}
