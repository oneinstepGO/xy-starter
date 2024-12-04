package com.oneinstep.starter.core.routing.spring;

import com.oneinstep.starter.core.routing.annotation.RouterInterface;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 路由bean BeanFactoryPostProcessor 处理器
 **/
@Component
public class RoutingInterfaceBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    /**
     * 获取该类实现的所有接口
     *
     * @param clazz 原类型
     * @return 该类实现的所有接口
     */
    public static Set<Class<?>> getAllInterfaces(Class<?> clazz) {
        Set<Class<?>> interfaces = new HashSet<>();
        while (clazz != null) {
            // 获取当前类实现的接口
            Class<?>[] currentInterfaces = clazz.getInterfaces();
            interfaces.addAll(Arrays.asList(currentInterfaces));

            // 获取父类
            clazz = clazz.getSuperclass();
        }
        return interfaces;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        Map<Class<?>, List<String>> routerInterfaceMap = new HashMap<>(8);
        String[] beanNames = configurableListableBeanFactory.getBeanNamesForAnnotation(RouterInterface.class);
        for (String beanName : beanNames) {
            Class<?> clazz = configurableListableBeanFactory.getType(beanName);
            if (clazz == null) {
                continue;
            }
            // 获取该类以及该类所有父类实现的接口
            Set<Class<?>> interfaces = getAllInterfaces(clazz);
            for (Class<?> interfaceClass : interfaces) {
                if (interfaceClass.getAnnotation(RouterInterface.class) != null) {
                    routerInterfaceMap.computeIfAbsent(interfaceClass, k -> new ArrayList<>()).add(beanName);
                }
            }
        }
        DefaultListableBeanFactory registry = (DefaultListableBeanFactory) configurableListableBeanFactory;
        routerInterfaceMap.forEach((interfaceClass, beanNameList) -> {
            if (CollectionUtils.isEmpty(beanNameList)) {
                return;
            }
            BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(RoutingInterfaceFactoryBean.class)
                    .setScope(BeanDefinition.SCOPE_SINGLETON)
                    .addConstructorArgValue(interfaceClass)
                    .addConstructorArgValue(beanNameList)
                    .getBeanDefinition();
            String interfaceName = interfaceClass.getSimpleName();
            interfaceName = interfaceName.substring(0, 1).toLowerCase() + interfaceName.substring(1);
            registry.registerBeanDefinition(interfaceName, beanDefinition);
            // 必须设置 被@RouterInterface 注解标注的接口的实现类 对应的 bean 为非Autowire候选人 setAutowireCandidate(false)，
            // 否则使用 @Autowired 或者 @Resources 注解注入被 @RouterInterface 注解的接口的时候会报找到多个bean 错误
            beanNameList.forEach(name -> registry.getBeanDefinition(name).setAutowireCandidate(false));
        });
    }

}
