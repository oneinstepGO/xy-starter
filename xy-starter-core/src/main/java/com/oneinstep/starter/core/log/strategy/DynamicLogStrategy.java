package com.oneinstep.starter.core.log.strategy;

/**
 * 动态日志策略，可基于 nacos 做动态日志级别调整
 * 如需改变策略，可覆写getLoggerLevel方法
 **/
public interface DynamicLogStrategy {

    /**
     * 获取日志级别 info | debug | error | warn
     * 大小写均可
     *
     * @return 日志级别
     */
    String getLoggerLevel();

}
