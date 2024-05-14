package com.oneinstep.starter.common.cache;

import lombok.Getter;

/**
 *
 */
@Getter
public enum CacheConfigEnum {

    /**
     * JWT 缓存
     */
    AUTH_JWT(CacheConstants.AUTH_JWT, 50, 1000, 180000),
    /**
     * 系统配置
     */
    SYS_CONFIG(CacheConstants.SYS_CONFIG, 10, 100, 300000);

    final String name;
    final int initSize;
    final int maxSize;
    final long expireMs;

    CacheConfigEnum(String name, int initSize, int maxSize, long expireMs) {
        this.name = name;
        this.initSize = initSize;
        this.maxSize = maxSize;
        this.expireMs = expireMs;
    }

}
