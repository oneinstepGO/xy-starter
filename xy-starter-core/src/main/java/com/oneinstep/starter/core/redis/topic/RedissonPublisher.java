package com.oneinstep.starter.core.redis.topic;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * redis 发布者
 */
@Component
@Slf4j
public class RedissonPublisher {

    @Resource
    private RedissonClient redissonClient;

    public void publishMessage(String message, String topic) {
        try {
            // 获取发布消息的主题
            RTopic rTopic = redissonClient.getTopic(topic);
            // 发布消息
            rTopic.publish(message);
        } catch (Exception e) {
            log.error("Failed to push message", e);
        }
    }

}
