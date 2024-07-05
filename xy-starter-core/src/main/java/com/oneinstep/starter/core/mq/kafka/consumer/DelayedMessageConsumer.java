package com.oneinstep.starter.core.mq.kafka.consumer;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.alibaba.fastjson2.JSONObject;
import com.oneinstep.starter.core.mq.kafka.delay.DelayedMessage;
import com.oneinstep.starter.core.mq.kafka.producer.KafkaProducerInstance;
import com.oneinstep.starter.core.utils.ExecutorServiceUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class DelayedMessageConsumer {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private KafkaProducerInstance kafkaProducerInstance;
    private static final int SHUTDOWN_TIMEOUT = 30;
    /**
     * 是否正在运行
     */
    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    /**
     * 线程池
     */
    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        executorService = new ThreadPoolExecutor(
                1,
                1,
                0,
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(),
                new ThreadFactoryBuilder().setNamePrefix("delayedMessageThreadPool-%d").build());
        executorService.execute(this::consume);
    }

    @PreDestroy
    public void destroy() {
        isRunning.set(false);
        ExecutorServiceUtil.shutdownExecutorService(executorService, "delayedMessageThreadPool", log);
    }

    public void consume() {
        RBlockingQueue<String> blockingDeque = redissonClient.getBlockingQueue("delayedMessageQueue");
        while (isRunning.get()) {
            try {
                String message = blockingDeque.take();
                if (StringUtils.isNotBlank(message)) {
                    DelayedMessage delayedMessage = JSONObject.parseObject(message, DelayedMessage.class);
                    if (delayedMessage != null) {
                        log.info("received the delayed message...message={}, execTime={}", delayedMessage.getMessage(), delayedMessage.getExecTime());
                        kafkaProducerInstance.sendMessage(delayedMessage.getTopic(), delayedMessage.getKey(), delayedMessage.getMessage());
                    }
                }
            } catch (InterruptedException ie) {
                log.error("thread interrupted.", ie);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("get delayed message error.", e);
            }
        }
    }

}