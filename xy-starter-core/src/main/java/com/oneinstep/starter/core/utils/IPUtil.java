package com.oneinstep.starter.core.utils;


import com.oneinstep.starter.core.constants.CommonConstant;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * IP工具类
 */
@UtilityClass
@Slf4j
public class IPUtil {
    /**
     * 获取请求的IP地址
     *
     * @param request 请求
     * @return IP地址
     */
    public static String getRemoteIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Real-IP");
        if (StringUtils.isBlank(ipAddress) || CommonConstant.UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1")) {
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    log.error("获取IP地址异常", e);
                }
                if (inet != null) {
                    ipAddress = inet.getHostAddress();
                }
            }
        }
        return ipAddress;
    }

    public static String getLocalIPAddress() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            log.error("获取本机IP地址异常", e);
        }
        return "";
    }

}
