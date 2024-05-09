package com.oneinstep.starter.security.constant;

import lombok.Getter;

@Getter
public enum SystemType {
    /**
     * 后台
     */
    ADMIN(1, "后台"),
    /**
     * 客户端
     */
    ORDINARY(2, "客户端");

    private final Integer code;
    private final String desc;

    SystemType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SystemType getByCode(Integer code) {
        for (SystemType value : SystemType.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid SystemType code: " + code);
    }
}
