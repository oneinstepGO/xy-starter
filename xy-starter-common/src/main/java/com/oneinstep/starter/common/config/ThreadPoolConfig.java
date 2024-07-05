package com.oneinstep.starter.common.config;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.oneinstep.starter.common.properties.ThreadPoolProperties;
import com.oneinstep.starter.core.utils.ExecutorServiceUtil;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池配置
 **/
@Configuration
@Slf4j
public class ThreadPoolConfig {

    private ExecutorService commonThreadPool;

    @Resource
    private ThreadPoolProperties threadPoolProperties;

    @Bean(name = "commonThreadPool")
    public ExecutorService commonThreadPool() {
        commonThreadPool = new ThreadPoolExecutor(threadPoolProperties.getCorePoolSize(),
                threadPoolProperties.getMaximumPoolSize(),
                threadPoolProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(threadPoolProperties.getQueueCapacity()),
                ThreadFactoryBuilder.create().setNamePrefix(threadPoolProperties.getNamePrefix() + "-").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
        return commonThreadPool;
    }

    @PreDestroy
    public void destroy() {
        ExecutorServiceUtil.shutdownExecutorService(commonThreadPool, "commonThreadPool", log);
        commonThreadPool = null;
    }

}
