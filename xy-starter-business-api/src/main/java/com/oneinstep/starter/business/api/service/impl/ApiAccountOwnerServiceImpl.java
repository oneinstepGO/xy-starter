package com.oneinstep.starter.business.api.service.impl;

import com.oneinstep.starter.business.api.bean.domain.User;
import com.oneinstep.starter.business.api.service.UserService;
import com.oneinstep.starter.core.utils.BeanCopyUtils;
import com.oneinstep.starter.security.bean.bo.TokenUserInfoBO;
import com.oneinstep.starter.security.constant.LoginMethodEnum;
import com.oneinstep.starter.security.constant.SystemType;
import com.oneinstep.starter.security.service.AccountOwnerService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@ConditionalOnProperty(name = "spring.application.name", havingValue = "xy-starter-api")
public class ApiAccountOwnerServiceImpl implements AccountOwnerService {

    @Resource
    private UserService userService;

    @Override
    public Optional<TokenUserInfoBO> queryUserByAccountId(@NotNull LoginMethodEnum loginMethod, @NotNull String accountId) {
        User user;
        if (loginMethod == LoginMethodEnum.EMAIL_PASSWORD) {
            user = userService.lambdaQuery().eq(User::getEmail, accountId).one();
        } else if (loginMethod == LoginMethodEnum.MOBILE_PASSWORD) {
            user = userService.lambdaQuery().eq(User::getMobile, accountId).one();
        } else {
            throw new IllegalArgumentException("账号格式不正确");
        }

        if (user == null) {
            return Optional.empty();
        }

        TokenUserInfoBO tokenUserInfoBO = BeanCopyUtils.copy(user, TokenUserInfoBO.class);
        tokenUserInfoBO.setPassword(user.getLoginPassword());
        tokenUserInfoBO.setAccountId(accountId);
        tokenUserInfoBO.setSystemType(SystemType.ORDINARY);
        return Optional.of(tokenUserInfoBO);
    }

    @Override
    public TokenUserInfoBO queryUserByUserId(Long userId) {

        User user = userService.getById(userId);
        if (user == null) {
            return null;
        }
        TokenUserInfoBO tokenUserInfoBO = BeanCopyUtils.copy(user, TokenUserInfoBO.class);
        tokenUserInfoBO.setPassword(user.getLoginPassword());
        tokenUserInfoBO.setSystemType(SystemType.ORDINARY);
        return tokenUserInfoBO;
    }

    @Override
    public List<String> queryPermsByUserId(Long accountOwnerId) {
        return Collections.emptyList();
    }

    @Override
    public void updateLastLoginTimeAndIp(Long userId, String loginIp) {
        User update = new User();
        update.setUserId(userId);
        update.setLastLoginTime(LocalDateTime.now());
        update.setLastLoginIp(loginIp);
        userService.updateById(update);
    }

    @Override
    public boolean updateLoginPassword(Long userId, String newPassword) {
        User update = new User();
        update.setUserId(userId);
        update.setLoginPassword(newPassword);
        return userService.updateById(update);
    }

    @Override
    public boolean updateGoogleSecretKey(Long userId, String secretKey) {
        User update = new User();
        update.setUserId(userId);
        update.setGoogleSecret(secretKey);
        return userService.updateById(update);
    }
}
