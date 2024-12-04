package com.oneinstep.starter.core.mq.kafka.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 重试消息
 */
@Getter
@Setter
@ToString
@Builder
public class RetryMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 主题
     */
    private String topic;
    /**
     * 消息
     */
    private String key;
    /**
     * 消息
     */
    private String message;
    /**
     * 重试次数
     */
    private int retryCount;
    /**
     * 最大重试次数
     */
    private int maxRetryCount;
    /**
     * 延时时间
     */
    private long nextDelayMs;
}
