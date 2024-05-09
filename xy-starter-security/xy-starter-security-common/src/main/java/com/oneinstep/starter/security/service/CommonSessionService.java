package com.oneinstep.starter.security.service;

import com.oneinstep.starter.security.constant.SystemType;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CommonSessionService {

    @Resource
    private JwtTokenStore jwtTokenStore;
    @Resource
    private TokenUserInfoStoreService tokenUserInfoStoreService;

    public void deleteToken(SystemType systemType, Long userId) {
        // 删除 jwt token
        jwtTokenStore.removeToken(systemType, userId);
        // 删除 token 用户信息
        tokenUserInfoStoreService.deleteUserInfo(systemType, userId);
    }

}
