package com.oneinstep.starter.security.service;

import com.oneinstep.starter.security.bean.bo.TokenUserInfoBO;
import com.oneinstep.starter.security.constant.LoginMethodEnum;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * 账户服务
 */
public interface AccountOwnerService {

    /**
     * 根据用户名查找用户
     *
     * @param accountId 用户名
     * @return 用户
     */
    Optional<TokenUserInfoBO> queryUserByAccountId(@NotNull LoginMethodEnum loginMethod, @NotNull String accountId);

    /**
     * 根据账户id查找账户
     *
     * @param userId 账户id
     * @return 用户
     */
    TokenUserInfoBO queryUserByUserId(@NotNull Long userId);

    /**
     * 查询用户的所有权限
     *
     * @param accountOwnerId 账户id
     * @return 权限列表
     */
    List<String> queryPermsByUserId(@NotNull Long accountOwnerId);

    /**
     * 修改账户最后登录时间和ip
     *
     * @param userId  用户id
     * @param loginIp 登录ip
     */
    void updateLastLoginTimeAndIp(@NotNull Long userId, String loginIp);

    /**
     * 修改密码
     *
     * @param userId      用户id
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean updateLoginPassword(Long userId, String newPassword);

    /**
     * 更新谷歌验证器密钥
     *
     * @param userId    账户id
     * @param secretKey 密钥
     */
    boolean updateGoogleSecretKey(@NotNull Long userId, String secretKey);

}
