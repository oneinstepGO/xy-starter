package com.oneinstep.starter.sys.service;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 *
 **/
public interface WsMsgQueueService {

    void pushMessageToQueue(@NotEmpty String queueName, @NotNull Long userId);

    String pollMessageFromQueue(@NotEmpty String queueName);

    void clearQueueMessage(@NotEmpty String queueName);

}
