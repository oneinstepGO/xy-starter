package com.oneinstep.starter.core.routing.annotation;

import java.lang.annotation.*;

/**
 * 路由接口路由规则
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RouterRule {

    String[] values();

}
