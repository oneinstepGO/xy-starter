package com.oneinstep.starter.sys.service.impl;

import com.oneinstep.starter.core.log.annotition.Logging;
import com.oneinstep.starter.sys.service.WsMsgQueueService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

/**
 * ws消息队列服务
 **/
@Service
@Slf4j
public class MsgQueueServiceImpl implements WsMsgQueueService {
    @Resource
    private RedissonClient redissonClient;
    private static final String MESSAGE_QUEUE_PREFIX = "message:queue:%s";

    @Override
    @Logging(printArgs = true, printResult = true)
    public void pushMessageToQueue(String queueName, Long userId) {
        RQueue<String> queue = redissonClient.getQueue(String.format(MESSAGE_QUEUE_PREFIX, queueName));
        queue.offer(String.valueOf(userId));
    }

    @Override
    @Logging(printArgs = true, printResult = true)
    public String pollMessageFromQueue(String queueName) {
        RQueue<String> queue = redissonClient.getQueue(String.format(MESSAGE_QUEUE_PREFIX, queueName));
        return queue.poll();
    }

    @Override
    @Logging(printArgs = true, printResult = true)
    public void clearQueueMessage(String queueName) {
        RQueue<String> queue = redissonClient.getQueue(String.format(MESSAGE_QUEUE_PREFIX, queueName));
        queue.delete();
    }
}
