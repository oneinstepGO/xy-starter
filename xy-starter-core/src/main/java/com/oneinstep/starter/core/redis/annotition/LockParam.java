package com.oneinstep.starter.core.redis.annotition;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LockParam {
    /**
     * 参数排序
     *
     * @return 参数排序
     */
    int value();
}
