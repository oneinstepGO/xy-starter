package com.oneinstep.starter.security.filter;

import com.oneinstep.starter.security.bean.bo.TokenUserInfoBO;
import com.oneinstep.starter.security.constant.SystemType;
import com.oneinstep.starter.security.context.AuthedUserInfoContext;
import com.oneinstep.starter.security.jwt.JwtTokenProvider;
import com.oneinstep.starter.security.service.AccountOwnerService;
import com.oneinstep.starter.security.service.JwtTokenStore;
import com.oneinstep.starter.security.service.TokenUserInfoStoreService;
import com.oneinstep.starter.security.utils.AuthorHeaderUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT token filter that handles all HTTP requests and checks if there is a valid JWT token.
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private JwtTokenProvider jwtTokenProvider;
    @Resource
    private JwtTokenStore jwtTokenStore;
    @Resource
    private TokenUserInfoStoreService tokenUserInfoStoreService;
    @Resource
    private AccountOwnerService accountOwnerService;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {

        try {
            // get JWT token from http request
            String jwt = AuthorHeaderUtil.getTokenFromRequest(request);

            // validate token
            if (StringUtils.isNotBlank(jwt) && jwtTokenProvider.validateToken(jwt)) {

                Integer accountType = jwtTokenProvider.getAccountType(jwt);
                SystemType systemType = SystemType.getByCode(accountType);

                // get userId from token
                Long userId = jwtTokenProvider.getUserId(jwt);

                String jwtFromRedis = jwtTokenStore.getJwtToken(systemType, userId);
                if (StringUtils.isBlank(jwtFromRedis) || !jwt.equals(jwtFromRedis)) {
                    log.info("token is expired");
                    filterChain.doFilter(request, response);
                    return;
                }

                // load the user associated with token
                UserDetails userDetails = accountOwnerService.queryUserByUserId(userId);

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                TokenUserInfoBO tokenUserInfoBO = tokenUserInfoStoreService.getUserInfo(systemType, userId);
                AuthedUserInfoContext.set(tokenUserInfoBO);

            }

            filterChain.doFilter(request, response);
        } finally {
            AuthedUserInfoContext.clean();
        }

    }

}
