package com.oneinstep.starter.security.jwt;

import com.alibaba.fastjson2.JSON;
import com.oneinstep.starter.core.error.SecurityCodeAndMsgError;
import com.oneinstep.starter.core.response.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * Jwt 验证入口点
 */
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.error("Unauthorized error: {}", authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);

        Result<Object> error = Result.error(SecurityCodeAndMsgError.UNAUTHORIZED);
        JSON.writeTo(response.getOutputStream(), error);

    }

}
