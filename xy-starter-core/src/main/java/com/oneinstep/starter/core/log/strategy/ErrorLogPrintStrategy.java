package com.oneinstep.starter.core.log.strategy;

import org.slf4j.Logger;

/**
 * info 日志打印策略实现
 **/
public class ErrorLogPrintStrategy implements LogPrintStrategy {

    @Override
    public void printLog(Logger logger, String message) {
        logger.error("{}", message);
    }

}
