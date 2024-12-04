package com.oneinstep.starter.sys.service;

import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;

/**
 * ws在线用户服务
 **/
public interface WsOnlineUserService {

    void addAndroidOnlineUser(String userId, WebSocketSession session);

    void addH5OnlineUser(String userId, WebSocketSession session);

    void removeAndroidOnlineUser(String userId);

    void removeH5OnlineUser(String userId);

    boolean isUserAndroidOnline(String userId);

    Long getAndroidOnlineUserCount();

    Long getH5OnlineUserCount();

    List<String> getAndroidOnlineUserList();

    List<String> getH5OnlineUserList();

    String getAndroidUserConnectServer(String userId);

    Map<String, String> getAndroidConnectServerMap();

    String getH5UserConnectServer(String userId);

    Map<String, String> getH5ConnectServerMap();
}
