package com.oneinstep.starter.core.mq.kafka;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.oneinstep.starter.core.mq.kafka.bean.RetryMessage;
import com.oneinstep.starter.core.mq.kafka.config.CustomKafkaProperties;
import com.oneinstep.starter.core.mq.kafka.producer.KafkaProducerInstance;
import com.oneinstep.starter.core.utils.ExecutorServiceUtil;
import com.oneinstep.starter.core.utils.RetryDelayCalculator;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Kafka 消费者抽象类
 */
@Component
@ConditionalOnProperty(prefix = "xy.starter.kafka", name = "enable", havingValue = "true")
public abstract class AbstractKafkaConsumer {

    @Resource
    private CustomKafkaProperties kafkaProperties;
    @Resource
    private KafkaProducerInstance kafkaProducerInstance;

    /**
     * 获取 Topic
     *
     * @return Topic
     */
    public abstract String getTopic();

    /**
     * 获取 GroupId
     *
     * @return GroupId
     */
    public abstract String getGroupId();

    /**
     * 处理消息
     *
     * @param message 消息
     */
    public abstract void handleMessage(String message);

    /**
     * 获取并发数
     *
     * @return 并发数
     */
    public abstract int getThreadNum();

    /**
     * 获取轮询超时时间
     *
     * @return 轮询超时时间
     */
    public abstract long getPollTimeout();

    /**
     * 消费线程最大空闲时间
     *
     * @return 消费线程最大空闲时间
     */
    public abstract long getMaxPollIntervalMs();

    /**
     * 获取日志
     *
     * @return 日志
     */
    public abstract Logger getLogger();

    /**
     * 是否重试
     *
     * @return 是否重试
     */
    public boolean needRetry() {
        return false;
    }

    /**
     * 最大重试次数
     *
     * @return 最大重试次数
     */
    public int maxRetryCount() {
        return 3;
    }

    /**
     * 初始化延迟时间
     *
     * @return 初始化延迟时间
     */
    public long initDelayMs() {
        return 5000L;
    }

    /**
     * 最大延迟时间
     *
     * @return 最大延迟时间
     */
    public long maxDelayMs() {
        return 60000L;
    }

    /**
     * 是否重试消费者
     *
     * @return 是否重试消费者
     */
    public boolean isRetryConsumer() {
        return false;
    }

    /**
     * 是否正在运行
     */
    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    @PostConstruct
    public void init() {
        if (StringUtils.isBlank(getTopic()) || StringUtils.isBlank(getGroupId())) {
            getLogger().error("Topic or GroupId can't be null.");
            throw new IllegalArgumentException("Topic or GroupId can't be null.");
        }
        Properties properties = initKafkaProperties();
        KafkaConsumerThread kafkaConsumerThread = new KafkaConsumerThread(properties, getThreadNum());
        kafkaConsumerThread.start();
    }

    private Properties initKafkaProperties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, getGroupId());
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, String.valueOf(getMaxPollIntervalMs()));
        return properties;
    }

    public class KafkaConsumerThread extends Thread {
        private final KafkaConsumer<String, String> kafkaConsumer;
        private final ExecutorService executorService;
        private final Map<TopicPartition, OffsetAndMetadata> offsets = new ConcurrentHashMap<>();

        public KafkaConsumerThread(Properties properties, int threadNum) {
            this.kafkaConsumer = new KafkaConsumer<>(properties);
            this.kafkaConsumer.subscribe(Lists.newArrayList(getTopic()), new ConsumerRebalanceListener() {
                @Override
                public void onPartitionsRevoked(Collection<TopicPartition> collection) {
                    getLogger().info("Lost partitions in re balance. Committing current offsets: {}", offsets);
                    commitOffsets();
                }

                @Override
                public void onPartitionsAssigned(Collection<TopicPartition> collection) {
                    getLogger().info("Partitions assigned in re balance: {}", collection);
                    offsets.clear();
                }
            });
            executorService = new ThreadPoolExecutor(
                    threadNum,
                    threadNum,
                    0L,
                    TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(1000),
                    ThreadFactoryBuilder.create().setNamePrefix("kafka-consumer-thread-" + getTopic() + "-").build(),
                    new ThreadPoolExecutor.CallerRunsPolicy());
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                getLogger().info("Shutdown hook is called.");
                isRunning.set(false);
                kafkaConsumer.wakeup();
                ExecutorServiceUtil.shutdownExecutorService(executorService, "kafka-consumer-thread", getLogger());
            }));
        }

        private void firstRetry(ConsumerRecord<String, String> message, String value) {
            getLogger().info("start to retry send message to retry topic.");
            String retryTopic = message.topic() + ".RETRY";
            RetryMessage retryMessage = RetryMessage.builder()
                    .topic(retryTopic)
                    .key(message.key())
                    .message(value)
                    .maxRetryCount(maxRetryCount())
                    .retryCount(1)
                    .nextDelayMs(initDelayMs())
                    .build();
            kafkaProducerInstance.sendMessage(retryTopic, message.key(), JSON.toJSONString(retryMessage), initDelayMs());
        }

        private void mayNeedRetryAgain(String topic, String value) {
            RetryMessage retryMessage = JSONObject.parseObject(value, RetryMessage.class);
            int maxRetryCount = retryMessage.getMaxRetryCount();
            int retryCount = retryMessage.getRetryCount();
            getLogger().info("handle retry message...TOPIC={}, retryCount={}，maxRetryCount={}", topic, retryCount, maxRetryCount);
            // 超过最大重试 发送到死信队列
            int newRetryCount = retryCount + 1;
            if (newRetryCount > maxRetryCount) {
                getLogger().error("Have Reached the max retry count....save to DEAD QUEUE.TOPIC={}", topic);
                String realTopic = topic.substring(0, topic.lastIndexOf(".RETRY"));
                kafkaProducerInstance.sendMessage(realTopic + ".DLT", null, retryMessage.getMessage());
            }
            // 继续重试
            else {
                getLogger().info("keep retry again. TOPIC={},tryCount={}", topic, newRetryCount);
                retryMessage.setRetryCount(newRetryCount);
                long newDelayMs = RetryDelayCalculator.calculateDelay(newRetryCount, maxRetryCount, initDelayMs(), maxDelayMs());
                retryMessage.setNextDelayMs(newDelayMs);
                kafkaProducerInstance.sendMessage(topic, retryMessage.getKey(), JSON.toJSONString(retryMessage), newDelayMs);
            }
        }

        @Override
        public void run() {
            try {
                while (isRunning.get()) {
                    ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(getPollTimeout()));
                    if (records.isEmpty()) {
                        continue;
                    }
                    getLogger().info("Received {} records.", records.count());

                    for (TopicPartition tp : records.partitions()) {
                        getLogger().info("Received records from partition: {}", tp.partition());
                        // 每个分区的消息
                        List<ConsumerRecord<String, String>> partitionRecords = records.records(tp);

                        for (ConsumerRecord<String, String> partitionRecord : partitionRecords) {
                            handleRecord(partitionRecord);
                        }

                        long lastConsumedOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
                        updateOffsets(tp, lastConsumedOffset);
                    }

                    commitOffsets();
                }
            } catch (WakeupException we) {
                // ignore for shutdown
                getLogger().warn("KafkaConsumerThread wakeup.");
            } catch (Exception e) {
                getLogger().error("KafkaConsumerThread run error", e);
            } finally {
                try {
                    getLogger().info("commitSync before KafkaConsumerThread close.");
                    kafkaConsumer.commitSync();
                } catch (Exception e) {
                    getLogger().error("KafkaConsumerThread close error", e);
                } finally {
                    getLogger().info("KafkaConsumerThread close.");
                    kafkaConsumer.close();
                }
            }
        }

        private void handleRecord(ConsumerRecord<String, String> consumerRecord) {
            try {
                getLogger().info("Received message: {} from partition: {} offset: {}", consumerRecord.value(), consumerRecord.partition(), consumerRecord.offset());
                String value = consumerRecord.value();
                executorService.submit(() -> {
                    try {
                        handleMessage(value);
                    } catch (Exception e) {
                        getLogger().error("KafkaConsumerThread handleMessage error", e);
                        handleRetry(consumerRecord, value);
                    }
                });
            } catch (Exception e) {
                getLogger().error("KafkaConsumerThread handleRecord error", e);
            }
        }

        /**
         * 处理重试
         *
         * @param consumerRecord 消息
         * @param value          消息内容
         */
        private void handleRetry(ConsumerRecord<String, String> consumerRecord, String value) {
            if (isRetryConsumer()) {
                getLogger().info("start to retry send message to retry topic.");
                mayNeedRetryAgain(consumerRecord.topic(), value);
            } else {
                if (needRetry()) {
                    getLogger().info("start to retry send message to retry topic.");
                    firstRetry(consumerRecord, value);
                }
            }
        }

        /**
         * 更新偏移量
         *
         * @param tp                 分区
         * @param lastConsumedOffset 最后消费的偏移量
         */
        private void updateOffsets(TopicPartition tp, long lastConsumedOffset) {
            offsets.compute(tp, (k, v) -> {
                if (v == null || lastConsumedOffset + 1 > v.offset()) {
                    return new OffsetAndMetadata(lastConsumedOffset + 1);
                }
                return v;
            });
        }

        /**
         * 提交偏移量
         */
        private void commitOffsets() {
            if (!offsets.isEmpty()) {
                getLogger().info("Committing offsets: {}", offsets);
                kafkaConsumer.commitSync(offsets);
                offsets.clear();
            }
        }
    }
}
