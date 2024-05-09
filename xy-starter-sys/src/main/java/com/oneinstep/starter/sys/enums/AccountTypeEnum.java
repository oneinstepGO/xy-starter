package com.oneinstep.starter.sys.enums;

import com.oneinstep.starter.security.constant.SecurityConstants;
import lombok.Getter;

/**
 * 账户类型
 **/
@Getter
public enum AccountTypeEnum {

    ADMIN(1, "后台用户"),
    USER(2, "用户");

    private final Integer code;

    private final String desc;

    AccountTypeEnum(final Integer code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 是否是管理员
     *
     * @param code 账户类型
     * @return 是否是管理员
     */
    public static boolean isNotAdmin(Integer code) {
        return !ADMIN.getCode().equals(code);
    }

    public static boolean isNotSuperAdmin(Long userId) {
        return SecurityConstants.SUPER_ADMIN_USER_ID != userId;
    }

}

