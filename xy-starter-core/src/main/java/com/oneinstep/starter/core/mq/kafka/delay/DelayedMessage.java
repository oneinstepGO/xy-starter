package com.oneinstep.starter.core.mq.kafka.delay;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
public class DelayedMessage implements Serializable {

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
     * 执行时间
     */
    private long execTime;

    public DelayedMessage(String topic, String message, long delayTime) {
        this.topic = topic;
        this.message = message;
        this.execTime = System.currentTimeMillis() + delayTime;
    }

}
