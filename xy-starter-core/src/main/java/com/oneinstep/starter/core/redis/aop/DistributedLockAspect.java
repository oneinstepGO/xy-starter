package com.oneinstep.starter.core.redis.aop;

import com.oneinstep.starter.core.exception.FailToGetLockException;
import com.oneinstep.starter.core.redis.DistributedLockUtil;
import com.oneinstep.starter.core.redis.annotition.EnableDistributedLock;
import com.oneinstep.starter.core.redis.annotition.LockParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@Aspect
public class DistributedLockAspect {

    @Pointcut("@annotation(com.oneinstep.starter.core.redis.annotition.EnableDistributedLock)")
    public void pointCut() {
    }

    @Around("pointCut() && @annotation(enableDistributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, EnableDistributedLock enableDistributedLock) throws Throwable {
        String lockPrefix = enableDistributedLock.lockPrefix();
        long lockTimeout = enableDistributedLock.lockTimeout();

        Signature signature = joinPoint.getSignature();
        String methodName = signature.getName();
        String className = joinPoint.getTarget().getClass().getName();

        // 获取方法的参数，看方法参数是否有被 @LockParam 注解标注，如果有则添加到 lockParamValues 中
        List<Object> lockParamValues = getLockParams(joinPoint);

        String lockName;
        if (StringUtils.isBlank(lockPrefix) || CollectionUtils.isEmpty(lockParamValues)) {
            lockName = className + "#" + methodName;
        } else {
            lockName = String.format(lockPrefix, lockParamValues.toArray());
        }

        return DistributedLockUtil.tryLockAndSupply(lockName, lockTimeout, () -> {
            try {
                return joinPoint.proceed();
            } catch (FailToGetLockException fe) {
                log.error("获取锁失败", fe);
                throw fe;
            } catch (Throwable throwable) {
                log.error("执行方法失败", throwable);
                throw new RuntimeException(throwable);
            }
        });

    }

    private static @NotNull List<Object> getLockParams(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        // 获取方法签名并进行强制转换
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Parameter[] parameters = methodSignature.getMethod().getParameters();

        Map<Integer, Object> lockParamMap = getLockParamMap(parameters, args);

        // 按照 lockParamMap 的 key 顺序添加到 lockParamValues 中
        return
                lockParamMap.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(Map.Entry::getValue).toList();
    }

    private static @NotNull Map<Integer, Object> getLockParamMap(Parameter[] parameters, Object[] args) {
        Map<Integer, Object> lockParamMap = new HashMap<>();
        // 遍历参数并检查是否带有 @LockParam 注解
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            LockParam lockParam = parameter.getAnnotation(LockParam.class);
            if (lockParam != null) {
                // 使用 LockParam 注解的值（如果有的话）来决定锁的名称中使用的参数
                Object arg = args[i];
                if (arg == null) {
                    throw new IllegalArgumentException("LockParam参数值不能为空");
                }
                lockParamMap.put(lockParam.value(), arg);
            }
        }
        return lockParamMap;
    }
}
