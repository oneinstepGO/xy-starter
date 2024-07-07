package com.oneinstep.starter.business.api.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.oneinstep.starter.core.mq.kafka.AbstractKafkaConsumer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DemoConsumer extends AbstractKafkaConsumer {

    @Resource
    private RedissonClient redissonClient;

    @Override
    public String getTopic() {
        return "kafka-topic-demo";
    }

    @Override
    public String getGroupId() {
        return "group-demo";
    }

    @Override
    public void handleMessage(String message) {
        log.info("正在处理消息:{}", message);
        try {
            JSONObject jsonObject = JSONObject.parse(message);
            Long id = jsonObject.getLong("id");
            boolean contains = redissonClient.getSet("already-consumed").contains(id);
            if (contains) {
                log.warn("消息已经被消费过:{}", id);
                return;
            }

            Thread.sleep(500);
            redissonClient.getSet("already-consumed").add(id);
        } catch (InterruptedException e) {
            log.error("处理消息异常", e);
        }
    }

    @Override
    public int getThreadNum() {
        return 3;
    }

    @Override
    public long getPollTimeout() {
        return 100;
    }

    @Override
    public long getMaxPollIntervalMs() {
        return 10000;
    }

    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public long maxDelayMs() {
        return 30000L;
    }

    @Override
    public long initDelayMs() {
        return 3000L;
    }

    @Override
    public int maxRetryCount() {
        return 5;
    }

    @Override
    public boolean needRetry() {
        return true;
    }

}
