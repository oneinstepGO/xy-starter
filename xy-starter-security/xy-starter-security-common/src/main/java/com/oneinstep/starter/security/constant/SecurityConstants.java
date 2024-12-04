package com.oneinstep.starter.security.constant;

public class SecurityConstants {

    private SecurityConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String STR_SYSTEM_TYPE = "systemType";

    public static final long SUPER_ADMIN_USER_ID = 1L;

    public static final long SUPER_ADMIN_ROLE_ID = -1L;

    public static final String ROLE_PREFIX = "ROLE_";
}
