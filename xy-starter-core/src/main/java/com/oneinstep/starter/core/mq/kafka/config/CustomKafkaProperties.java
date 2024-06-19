package com.oneinstep.starter.core.mq.kafka.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * kafka 配置
 */
@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "xy.starter.kafka")
@Component
public class CustomKafkaProperties {

    /**
     * 是否启用
     */
    private boolean enable = false;
    /**
     * kafka 服务器地址
     */
    private String bootstrapServers = "localhost:9092";

}
