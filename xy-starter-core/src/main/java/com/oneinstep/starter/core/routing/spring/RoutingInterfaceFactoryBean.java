package com.oneinstep.starter.core.routing.spring;

import com.oneinstep.starter.core.routing.annotation.RouterRule;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 路由bean 代理类生成工厂
 **/
public class RoutingInterfaceFactoryBean<T> implements FactoryBean<T>, ApplicationContextAware {

    /**
     * 代理的接口
     */
    private final Class<T> interfaceClass;
    /**
     * 接口的实现类 beanNameList
     */
    private final List<String> beanNameList;
    /**
     * Spring 容器
     */
    private ApplicationContext applicationContext;

    public RoutingInterfaceFactoryBean(final Class<T> interfaceClass, final List<String> beanNameList) {
        this.interfaceClass = interfaceClass;
        this.beanNameList = beanNameList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getObject() {
        List<T> beanList = beanNameList.stream().map(name -> (T) applicationContext.getBean(name))
                .filter(bean -> {
                    Class<?> ultimateTargetClass = AopProxyUtils.ultimateTargetClass(bean);
                    return ultimateTargetClass.isAnnotationPresent(RouterRule.class);
                })
                .toList();
        if (CollectionUtils.isEmpty(beanList)) {
            throw new IllegalStateException("No candidate bean for interface: " + interfaceClass.getSimpleName());
        }
        return RoutingBeanProxyFactory.createProxy(interfaceClass, beanList);
    }

    @Override
    public Class<T> getObjectType() {
        return interfaceClass;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
