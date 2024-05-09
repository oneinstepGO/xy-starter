package com.oneinstep.starter.core.utils;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

/**
 * uri 工具类
 **/
@UtilityClass
public class UriUtil {

    public static Map<String, String> getQueryMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<>();
        for (String param : params) {
            String[] nameValue = param.split("=");
            if (nameValue.length != 2) {
                throw new IllegalArgumentException("Invalid query: " + query);
            }
            map.put(nameValue[0], nameValue[1]);
        }
        return map;
    }

}
