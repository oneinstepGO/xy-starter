package com.oneinstep.starter.core.log.aop;

import com.alibaba.fastjson2.JSON;
import com.oneinstep.starter.core.exception.OneBaseException;
import com.oneinstep.starter.core.log.annotition.Logging;
import com.oneinstep.starter.core.log.enums.PrintLogLevel;
import com.oneinstep.starter.core.log.factory.LogPrintStrategyFactory;
import com.oneinstep.starter.core.log.strategy.DynamicLogStrategy;
import com.oneinstep.starter.core.log.strategy.LogPrintStrategy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


/**
 * 打印日志的切面
 **/
@Component
@Slf4j
@Aspect
@Order(Integer.MIN_VALUE)
public class LoggingAspect {

    @Pointcut("@annotation(com.oneinstep.starter.core.log.annotition.Logging)")
    public void pointCut() {
    }

    @Autowired(required = false)
    private DynamicLogStrategy dynamicLogStrategy;

    @Around("pointCut()")
    public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        String className = joinPoint.getTarget().getClass().getName();
        String path = className + "#" + methodName;
        Logging annotation = signature.getMethod().getAnnotation(Logging.class);

        PrintLogLevel printLogLevel = getPrintLogLevel(annotation);
        LogPrintStrategy logPrintStrategy = LogPrintStrategyFactory.createLogPrintStrategy(printLogLevel);

        if (annotation.printArgs()) {
            Object[] args = joinPoint.getArgs();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                // 忽略 HttpServletRequest 和 HttpServletResponse
                if (arg instanceof HttpServletRequest || arg instanceof HttpServletResponse) {
                    continue;
                }
                if (arg != null) {
                    stringBuilder.append(JSON.toJSONString(arg));
                } else {
                    stringBuilder.append("null");
                }
                if (i != args.length - 1) {
                    stringBuilder.append(" || ");
                }
            }
            String msg = stringBuilder.toString();
            stringBuilder.setLength(0);
            logPrintStrategy.printLog(log, String.format("[Entering]: [%s] with arguments: << %s >>", path, msg));
        } else {
            logPrintStrategy.printLog(log, String.format("[Entering]: [%s] ", path));
        }
        Object result = null;
        Throwable throwable = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable t) {
            throwable = t;
            throw t;
        } finally {
            try {
                long time = System.currentTimeMillis() - start;
                if (throwable == null) {
                    if (annotation.printResult()) {
                        Class<?> returnType = signature.getReturnType();
                        if (returnType.equals(Void.TYPE)) {
                            logPrintStrategy.printLog(log, String.format("[Exiting]: [%s] success, the method return void, cost: %d ms", path, time));
                        } else {
                            logPrintStrategy.printLog(log, String.format("[Exiting]: [%s] success with result: << %s >>, cost: %d ms", path, result == null ? "null" : JSON.toJSONString(result), time));
                        }
                    } else {
                        logPrintStrategy.printLog(log, String.format("[Exiting]: [%s] success, cost: %d ms", path, time));
                    }
                } else {
                    if (annotation.printError()) {
                        if (throwable instanceof OneBaseException hstBaseException) {
                            log.error("[Exiting]: [{}] with error, code={}, msg={}, cost: {} ms", path, hstBaseException.getCode(), hstBaseException.getMessage(), time);
                        } else {
                            log.error("[Exiting]: [{}] with error, cost: {} ms", path, time, throwable);
                        }
                    } else {
                        log.error("[Exiting]: [{}] with error, cost: {} ms", path, time);
                    }
                }
            } catch (Throwable t1) {
                //ignored
                log.error("unexceptional error", t1);
            }
        }
        return result;
    }

    private PrintLogLevel getPrintLogLevel(Logging annotation) {
        PrintLogLevel printLogLevel;
        if (dynamicLogStrategy != null && StringUtils.hasText(dynamicLogStrategy.getLoggerLevel())) {
            printLogLevel = PrintLogLevel.of(dynamicLogStrategy.getLoggerLevel());
            log.debug("Use the dynamic log strategy. the log level is {}", printLogLevel.name());
        } else {
            printLogLevel = annotation.logLevel();
        }
        return printLogLevel;
    }

}
