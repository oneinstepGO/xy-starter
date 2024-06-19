package com.oneinstep.starter.business.api.consumer;

import com.oneinstep.starter.core.mq.kafka.AbstractKafkaConsumer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DemoConsumer extends AbstractKafkaConsumer {

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
}
