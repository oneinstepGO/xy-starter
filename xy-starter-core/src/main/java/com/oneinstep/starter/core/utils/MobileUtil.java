package com.oneinstep.starter.core.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class MobileUtil {

    /**
     * 获取手机号的后四位
     */
    public static String getMobileLastFour(String mobile) {
        if (mobile == null || mobile.length() < 4) {
            log.error("手机号码长度不足4位");
            return null;
        }
        return mobile.substring(mobile.length() - 4);
    }

}
