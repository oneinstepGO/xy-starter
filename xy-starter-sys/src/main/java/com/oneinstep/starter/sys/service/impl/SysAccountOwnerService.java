package com.oneinstep.starter.sys.service.impl;

import com.oneinstep.starter.core.utils.BeanCopyUtils;
import com.oneinstep.starter.security.bean.bo.TokenUserInfoBO;
import com.oneinstep.starter.security.constant.LoginMethodEnum;
import com.oneinstep.starter.security.constant.SystemType;
import com.oneinstep.starter.security.service.AccountOwnerService;
import com.oneinstep.starter.sys.bean.domain.SysUser;
import com.oneinstep.starter.sys.service.SysUserService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@ConditionalOnProperty(name = "spring.application.name", havingValue = "xy-starter-admin")
public class SysAccountOwnerService implements AccountOwnerService {

    @Resource
    private SysUserService sysUserService;

    @Override
    public Optional<TokenUserInfoBO> queryUserByAccountId(@NotNull LoginMethodEnum loginMethod, @NotNull String accountId) {
        SysUser sysUser = sysUserService.lambdaQuery().eq(SysUser::getUsername, accountId).one();
        if (sysUser == null) {
            return Optional.empty();
        }

        Long userId = sysUser.getUserId();

        TokenUserInfoBO tokenUserInfoBO = BeanCopyUtils.copy(sysUser, TokenUserInfoBO.class);

        // 设置角色
        List<Long> roleIds = sysUserService.getRoleIdsByUserId(userId);
        tokenUserInfoBO.setRoleIds(roleIds);

        // 设置权限
        tokenUserInfoBO.setPerms(queryPermsByUserId(userId));

        // 账户ID 为 用户名
        tokenUserInfoBO.setAccountId(accountId);
        tokenUserInfoBO.setSystemType(SystemType.ADMIN);

        return Optional.of(tokenUserInfoBO);
    }

    @Override
    public TokenUserInfoBO queryUserByUserId(Long userId) {
        SysUser sysUser = sysUserService.getById(userId);

        TokenUserInfoBO tokenUserInfoBO = BeanCopyUtils.copy(sysUser, TokenUserInfoBO.class);
        List<Long> roleIds = sysUserService.getRoleIdsByUserId(userId);
        tokenUserInfoBO.setRoleIds(roleIds);

        tokenUserInfoBO.setPerms(queryPermsByUserId(userId));

        // 账户ID 为 用户名
        tokenUserInfoBO.setAccountId(sysUser.getUsername());
        tokenUserInfoBO.setSystemType(SystemType.ADMIN);

        return tokenUserInfoBO;
    }

    @Override
    public List<String> queryPermsByUserId(Long accountOwnerId) {
        return sysUserService.getPermsByUserId(accountOwnerId);
    }

    @Override
    public void updateLastLoginTimeAndIp(Long userId, String loginIp) {
        SysUser update = new SysUser();
        update.setUserId(userId);
        update.setLastLoginTime(LocalDateTime.now());
        update.setLastLoginIp(loginIp);
        sysUserService.updateById(update);
    }

    @Override
    public boolean updateLoginPassword(Long userId, String newPassword) {
        SysUser update = new SysUser();
        update.setUserId(userId);
        update.setPassword(newPassword);
        return sysUserService.updateById(update);
    }

    @Override
    public boolean updateGoogleSecretKey(Long userId, String secretKey) {
        SysUser update = new SysUser();
        update.setUserId(userId);
        update.setGoogleSecret(secretKey);
        return sysUserService.updateById(update);
    }
}
