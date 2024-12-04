package com.oneinstep.starter.core.routing.spring;

import com.oneinstep.starter.core.routing.annotation.RouterRule;
import com.oneinstep.starter.core.routing.annotation.RoutingKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 路由 bean 代理类生成工厂
 *
 * @see RoutingInterfaceBeanFactoryPostProcessor
 **/
@Slf4j
public class RoutingBeanProxyFactory {

    private static final String TO_STRING = "toString";
    private static final String HASH_CODE = "hashCode";
    private static final String EQUALS = "equals";

    private RoutingBeanProxyFactory() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Class<T> targetClass, List<T> beans) {
        return (T) Proxy.newProxyInstance(targetClass.getClassLoader(), new Class[]{targetClass}, new ParamRoutingInvocationHandler<>(beans));
    }

    private static class ParamRoutingInvocationHandler<T> implements InvocationHandler {
        private final List<T> beans;

        public ParamRoutingInvocationHandler(final List<T> beans) {
            this.beans = beans;
        }

        /**
         * 获取路由参数值
         *
         * @param method    调用方法
         * @param arguments 参数列表
         * @return 被RoutingKey注解标注的参数值
         */
        private static String getRoutingValue(Method method, Object[] arguments) {
            Parameter[] parameters = method.getParameters();
            String routingValue = null;
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i].isAnnotationPresent(RoutingKey.class)) {
                    if (i >= arguments.length) {
                        throw new IllegalStateException("Arguments index out of range,method = " + method.getName());
                    }
                    Object argument = arguments[i];
                    if (argument == null) {
                        throw new IllegalStateException("RoutingKey param value can't be null.");
                    }
                    if (!(ClassUtils.isPrimitiveOrWrapper(argument.getClass()) || (argument instanceof String))) {
                        throw new IllegalArgumentException("RoutingKey param value must be Primitive type or String.");
                    }
                    routingValue = String.valueOf(argument).trim();
                    break;
                }
            }
            if (!StringUtils.hasText(routingValue)) {
                throw new IllegalStateException("RoutingKey param value is blank,method = " + method.getName());
            }
            return routingValue;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Class<?>[] parameterTypes = method.getParameterTypes();
            String methodName = method.getName();
            if (parameterTypes.length == 0) {
                if (TO_STRING.equals(methodName)) {
                    return new Object().toString();
                } else if (HASH_CODE.equals(methodName)) {
                    return new Object().hashCode();
                }
            } else if (parameterTypes.length == 1 && EQUALS.equals(methodName)) {
                return new Object().equals(args[0]);
            }

            try {
                return method.invoke(getTargetBean(getRoutingValue(method, args), this.beans), args);
            } catch (InvocationTargetException invocationTargetException) {
                throw invocationTargetException.getCause();
            }
        }

        private Object getTargetBean(String routingValue, List<T> beans) {
            if (!StringUtils.hasText(routingValue)) {
                throw new IllegalStateException("routingValue Can't be empty.");
            }
            Object target = null;
            for (Object bean : beans) {
                Class<?> ultimateTargetClass = AopProxyUtils.ultimateTargetClass(bean);
                // 判断是否是 cglib 代理Bean
                RouterRule routerRule = ultimateTargetClass.getAnnotation(RouterRule.class);
                if (routerRule != null) {
                    String[] values = routerRule.values();
                    if (values != null && values.length != 0 && (Stream.of(values).anyMatch(v -> Objects.equals(v.trim(), routingValue)))) {
                        target = bean;
                        break;
                    }
                }
            }
            if (target == null) {
                throw new IllegalStateException("Can't find any target bean.");
            }
            return target;
        }

    }

}
