package com.oneinstep.starter.core.routing.annotation;

import java.lang.annotation.*;

/**
 * 路由接口路由参数标注
 **/
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RoutingKey {

}
