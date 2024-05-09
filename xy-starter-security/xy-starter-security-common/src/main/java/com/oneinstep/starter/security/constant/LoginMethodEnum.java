package com.oneinstep.starter.security.constant;

import lombok.Getter;

/**
 * 登录方式枚举
 */
@Getter
public enum LoginMethodEnum {
    /**
     * 用户名 + 密码 登陆
     */
    USERNAME_PASSWORD(1),
    /**
     * 手机号 + 密码 登陆
     */
    MOBILE_PASSWORD(2),
    /**
     * 邮箱 + 密码 登陆
     */
    EMAIL_PASSWORD(3),
    /**
     * 手机 + 验证码 登陆
     */
    SMS(4),
    /**
     * 微信登陆
     */
    WECHAT(5);

    private final int code;

    LoginMethodEnum(int code) {
        this.code = code;
    }

    public static LoginMethodEnum getByCode(int code) {
        for (LoginMethodEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }

}
