package com.oneinstep.starter.core.trace;

import com.oneinstep.starter.core.constants.CommonConstant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Component
public class RequestIdInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        // 在 preHandle 方法中生成 reqId 并将其存储在当前请求的属性中
        String reqId = UUID.randomUUID().toString();
        MDC.put(CommonConstant.REQUEST_ID, reqId);
        return true;
    }

    @Override
    public void postHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, ModelAndView modelAndView) {
        // 在 postHandle 方法中从请求属性中获取 reqId，并将其从属性中移除
        try {
            MDC.remove(CommonConstant.REQUEST_ID);
        } finally {
            MDC.clear();
        }
    }

}

