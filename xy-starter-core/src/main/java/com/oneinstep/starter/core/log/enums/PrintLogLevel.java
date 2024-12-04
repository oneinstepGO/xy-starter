package com.oneinstep.starter.core.log.enums;

import java.util.stream.Stream;

/**
 * 日志打印级别
 */

public enum PrintLogLevel {
    /**
     * log.info()
     */
    INFO,
    /**
     * log.debug()
     */
    DEBUG,
    /**
     * log.warn()
     */
    WARN,
    /**
     * log.error()
     */
    ERROR;

    /**
     * 获取打印日志级别
     *
     * @param level 日志级别字符串
     * @return 打印日志级别 PrintLogLevel
     */
    public static PrintLogLevel of(String level) {
        return Stream.of(PrintLogLevel.values()).filter(e -> e.name().equalsIgnoreCase(level)).findFirst().orElse(INFO);
    }
}