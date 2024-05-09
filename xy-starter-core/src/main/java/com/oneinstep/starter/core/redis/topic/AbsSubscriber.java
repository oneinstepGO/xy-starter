package com.oneinstep.starter.core.redis.topic;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * redis 订阅者抽象类
 */
@Component
@Slf4j
public abstract class AbsSubscriber {

    @Resource
    private RedissonClient redissonClient;

    public abstract String getTopic();

    public abstract void handleMessage(String message);

    @PostConstruct
    public void init() {
        subscribeToTopic();
    }

    private void subscribeToTopic() {
        // 获取订阅消息的主题
        RTopic rTopic = redissonClient.getTopic(getTopic());

        // 订阅消息
        rTopic.addListener(String.class, (charSequence, s) -> {
            log.info("Received message: {}", s);
            handleMessage(s);
        });
    }
}
