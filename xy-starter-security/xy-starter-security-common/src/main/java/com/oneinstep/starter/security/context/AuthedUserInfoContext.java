package com.oneinstep.starter.security.context;

import com.oneinstep.starter.security.bean.bo.TokenUserInfoBO;

/**
 * 认证后的用户上下文
 */
public class AuthedUserInfoContext {

    private AuthedUserInfoContext() {
        throw new IllegalStateException("Utility class");
    }

    private static final ThreadLocal<TokenUserInfoBO> USER_INFO_IN_TOKEN_HOLDER = new ThreadLocal<>();

    public static TokenUserInfoBO get() {
        return USER_INFO_IN_TOKEN_HOLDER.get();
    }

    public static void set(TokenUserInfoBO tokenUserInfoBO) {
        USER_INFO_IN_TOKEN_HOLDER.set(tokenUserInfoBO);
    }

    public static void clean() {
        if (USER_INFO_IN_TOKEN_HOLDER.get() != null) {
            USER_INFO_IN_TOKEN_HOLDER.remove();
        }
    }

}