package com.oneinstep.starter.security.authorize;

import com.alibaba.fastjson2.JSON;
import com.oneinstep.starter.core.error.SecurityCodeAndMsgError;
import com.oneinstep.starter.core.response.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 拒绝访问（无权限）处理器
 */
@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        log.error("Access Denied....msg: {}", accessDeniedException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);

        Result<Object> error = Result.error(SecurityCodeAndMsgError.UNAUTHORIZED);
        JSON.writeTo(response.getOutputStream(), error);
    }

}
