package com.oneinstep.starter.core.mq.kafka.producer;

import com.alibaba.fastjson2.JSON;
import com.oneinstep.starter.core.mq.kafka.delay.DelayedMessage;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Kafka 生产者实例
 */
@Slf4j
@Component
public class KafkaProducerInstance {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private KafkaProperties kafkaProperties;
    private KafkaProducer<String, String> producer;

    @PostConstruct
    public void init() {
        log.info("KafkaProducerInstance init.");
        // 初始化
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        this.producer = new KafkaProducer<>(properties);
    }

    public void sendMessage(String topic, String key, String message, long delayedMs) {
        try {
            if (delayedMs > 0) {
                log.info("Send message later. delayedMs={}, message={}", delayedMs, message);
                RBlockingQueue<String> blockingDeque = redissonClient.getBlockingQueue("delayedMessageQueue");
                RDelayedQueue<String> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
                delayedQueue.offer(JSON.toJSONString(new DelayedMessage(topic, message, delayedMs)), delayedMs, TimeUnit.MILLISECONDS);
                delayedQueue.destroy();
            } else {
                ProducerRecord<String, String> r = new ProducerRecord<>(topic, key, message);
                log.info("Send message right now.message={}", message);
                this.producer.send(r);
            }
        } catch (Exception e) {
            log.error("Send message error.", e);
        }
    }

    public void sendMessage(String topic, String key, String message) {
        sendMessage(topic, key, message, 0);
    }

}
