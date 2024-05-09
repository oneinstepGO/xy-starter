package com.oneinstep.starter.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 线程池配置
 **/
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "xy.starter.thread-pool")
public class ThreadPoolProperties {
    /**
     * 核心线程数
     */
    private int corePoolSize = 10;
    /**
     * 最大线程数
     */
    private int maximumPoolSize = 500;

    /**
     * 线程存活时间
     */
    private long keepAliveTime = 60L;

    /**
     * 队列容量
     */
    private int queueCapacity = 1000;

    /**
     * 线程名称前缀
     */
    private String namePrefix = "common-thread-pool";

}
