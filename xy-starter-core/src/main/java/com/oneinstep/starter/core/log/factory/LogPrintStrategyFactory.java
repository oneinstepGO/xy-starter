package com.oneinstep.starter.core.log.factory;

import com.oneinstep.starter.core.log.enums.PrintLogLevel;
import com.oneinstep.starter.core.log.strategy.*;
import lombok.experimental.UtilityClass;

/**
 * 创建日志打印策略工厂
 **/
@UtilityClass
public class LogPrintStrategyFactory {

    /**
     * 创建 日志打印策略
     *
     * @param logLevel 日志级别
     * @return 日志打印策略
     */
    public static LogPrintStrategy createLogPrintStrategy(PrintLogLevel logLevel) {
        return switch (logLevel) {
            case INFO -> new InfoLogPrintStrategy();
            case DEBUG -> new DebugLogPrintStrategy();
            case WARN -> new WarnLogPrintStrategy();
            case ERROR -> new ErrorLogPrintStrategy();
        };
    }

}
