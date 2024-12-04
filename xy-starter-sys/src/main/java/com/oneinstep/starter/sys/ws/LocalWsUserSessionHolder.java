package com.oneinstep.starter.sys.ws;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地用户session持有者
 **/
public class LocalWsUserSessionHolder {

    private LocalWsUserSessionHolder() {
    }

    private static final Map<String, WebSocketSession> NOT_LOGIN_USER_SESSION_MAP = new ConcurrentHashMap<>();
    private static final Map<String, WebSocketSession> ANDROID_LOGIN_USER_SESSION_MAP = new ConcurrentHashMap<>();
    private static final Map<String, WebSocketSession> H5_LOGIN_USER_SESSION_MAP = new ConcurrentHashMap<>();

    public static void putUserNotLoginSession(String userId, WebSocketSession session) {
        if (StringUtils.isNotBlank(userId) && session != null) {
            NOT_LOGIN_USER_SESSION_MAP.put(userId, session);
        }
    }

    public static WebSocketSession removeUserNotLoginSession(String sessionId) {
        if (StringUtils.isNotBlank(sessionId)) {
            return NOT_LOGIN_USER_SESSION_MAP.remove(sessionId);
        }
        return null;
    }

    public static WebSocketSession getUserNotLoginSession(String userId) {
        return NOT_LOGIN_USER_SESSION_MAP.get(userId);
    }

    public static void putAndroidLoginUserSession(String userId, WebSocketSession session) {
        ANDROID_LOGIN_USER_SESSION_MAP.put(userId, session);
    }

    public static WebSocketSession removeAndroidLoginUserSession(String userId) {
        if (StringUtils.isNotBlank(userId)) {
            return ANDROID_LOGIN_USER_SESSION_MAP.remove(userId);
        }
        return null;
    }

    public static void putH5LoginUserSession(String userId, WebSocketSession session) {
        H5_LOGIN_USER_SESSION_MAP.put(userId, session);
    }

    public static WebSocketSession removeH5LoginUserSession(String userId) {
        if (StringUtils.isNotBlank(userId)) {
            return H5_LOGIN_USER_SESSION_MAP.remove(userId);
        }
        return null;
    }

    public static WebSocketSession getAndroidLoginUserSession(String userId) {
        return ANDROID_LOGIN_USER_SESSION_MAP.get(userId);
    }

    public static WebSocketSession getH5LoginUserSession(String userId) {
        return H5_LOGIN_USER_SESSION_MAP.get(userId);
    }

    public static List<String> getAndroidOnlineUserIds() {
        return new ArrayList<>(ANDROID_LOGIN_USER_SESSION_MAP.keySet());
    }

    public static List<String> getH5OnlineUserIds() {
        return new ArrayList<>(H5_LOGIN_USER_SESSION_MAP.keySet());
    }

    public static List<WebSocketSession> getAndroidOnlineUserSessions() {
        return new ArrayList<>(ANDROID_LOGIN_USER_SESSION_MAP.values());
    }

    public static List<WebSocketSession> getNotLoginUserSessions() {
        return new ArrayList<>(NOT_LOGIN_USER_SESSION_MAP.values());
    }

    public static List<WebSocketSession> getH5LoginUserSessions() {
        return new ArrayList<>(H5_LOGIN_USER_SESSION_MAP.values());
    }


}
