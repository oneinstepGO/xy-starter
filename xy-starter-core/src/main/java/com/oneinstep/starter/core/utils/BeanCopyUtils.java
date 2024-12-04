package com.oneinstep.starter.core.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

/**
 * bean copy 工具类
 */
@UtilityClass
@Slf4j
public class BeanCopyUtils {

    public <T> T copy(Object source, Class<T> target) {
        T t = null;
        try {
            t = target.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("BeanCopyUtils copy error", e);
        }

        if (t != null) {
            BeanUtils.copyProperties(source, t);
        }

        return t;
    }
}
