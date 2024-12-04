package com.oneinstep.starter.core.utils;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ExecutorService工具类
 */
@UtilityClass
public class ExecutorServiceUtil {

    private static final int DEFAULT_SHUTDOWN_TIMEOUT = 60; // 默认的关闭超时时间，可以根据需要调整

    /**
     * 关闭线程池
     *
     * @param executorService     线程池
     * @param executorServiceName 线程池名称
     * @param logger              日志
     */
    public static void shutdownExecutorService(ExecutorService executorService, String executorServiceName, final Logger logger) {
        if (executorService == null) {
            return;
        }
        logger.info("正在关闭线程池 {}", executorServiceName);
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(DEFAULT_SHUTDOWN_TIMEOUT, TimeUnit.SECONDS)) {
                List<Runnable> canceledRunnable = executorService.shutdownNow();
                logger.warn("线程池 {} 未能正常关闭，剩余任务数：{}", executorServiceName, canceledRunnable.size());
            }
        } catch (InterruptedException e) {
            logger.error("线程池 {} 关闭异常", executorServiceName, e);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
