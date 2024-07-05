package com.oneinstep.starter.common.config;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.oneinstep.starter.common.properties.ThreadPoolProperties;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
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

    private static final int SHUTDOWN_TIMEOUT = 30;

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
        if (commonThreadPool == null) {
            return;
        }
        log.info("关闭线程池 {}", threadPoolProperties.getNamePrefix());
        commonThreadPool.shutdown();
        try {
            if (!commonThreadPool.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.SECONDS)) {
                List<Runnable> canceledRunnable = commonThreadPool.shutdownNow();
                log.warn("线程池 {} 未能正常关闭，剩余任务数：{}", threadPoolProperties.getNamePrefix(), canceledRunnable.size());
            }
        } catch (InterruptedException e) {
            log.error("线程池 {} 关闭异常", threadPoolProperties.getNamePrefix(), e);
            commonThreadPool.shutdownNow();
            Thread.currentThread().interrupt();
        } finally {
            commonThreadPool = null;
        }
    }

}
