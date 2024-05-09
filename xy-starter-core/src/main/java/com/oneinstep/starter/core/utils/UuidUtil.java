package com.oneinstep.starter.core.utils;

import lombok.experimental.UtilityClass;

/**
 * uuid 工具类
 **/
@UtilityClass
public class UuidUtil {

    /**
     * 生成 去除了 - 的 uuid
     *
     * @return uuid
     */
    public static String uuid() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }

}
