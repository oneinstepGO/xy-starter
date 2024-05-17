package com.oneinstep.starter.sys.service.impl;

import com.oneinstep.starter.core.utils.IPUtil;
import com.oneinstep.starter.sys.service.WsOnlineUserService;
import com.oneinstep.starter.sys.ws.LocalWsUserSessionHolder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;

/**
 * 在线用户服务
 **/
@Slf4j
@Service
public class WsOnlineUserServiceImpl implements WsOnlineUserService {

    @Resource
    private RedissonClient redissonClient;

    private static final String ANDROID_ONLINE_USER_SET_KEY = "ws:online:user:set";
    private static final String H5_ONLINE_USER_SET_KEY = "ws:online:user:h5:set";

    private static final String ANDROID_ONLINE_USER_CONNECT_HASH = "ws:online:user:connect:server:hash";
    private static final String H5_ONLINE_USER_CONNECT_HASH = "ws:online:user:h5:connect:server:hash";

    @Override
    public void addAndroidOnlineUser(String userId, WebSocketSession session) {
        LocalWsUserSessionHolder.putAndroidLoginUserSession(userId, session);
        RScoredSortedSet<String> scoredSortedSet = redissonClient.getScoredSortedSet(ANDROID_ONLINE_USER_SET_KEY);
        scoredSortedSet.add(System.currentTimeMillis(), userId);

        RMap<String, String> map = redissonClient.getMap(ANDROID_ONLINE_USER_CONNECT_HASH);
        map.put(userId, IPUtil.getLocalIPAddress());
    }

    @Override
    public void addH5OnlineUser(String userId, WebSocketSession session) {
        LocalWsUserSessionHolder.putH5LoginUserSession(userId, session);
        RScoredSortedSet<String> scoredSortedSet = redissonClient.getScoredSortedSet(H5_ONLINE_USER_SET_KEY);
        scoredSortedSet.add(System.currentTimeMillis(), userId);

        RMap<String, String> map = redissonClient.getMap(H5_ONLINE_USER_CONNECT_HASH);
        map.put(userId, IPUtil.getLocalIPAddress());
    }

    @Override
    public void removeAndroidOnlineUser(String userId) {
        RScoredSortedSet<String> scoredSortedSet = redissonClient.getScoredSortedSet(ANDROID_ONLINE_USER_SET_KEY);
        scoredSortedSet.remove(userId);

        RMap<String, String> map = redissonClient.getMap(ANDROID_ONLINE_USER_CONNECT_HASH);
        map.remove(userId);
    }

    @Override
    public void removeH5OnlineUser(String userId) {
        RScoredSortedSet<String> scoredSortedSet = redissonClient.getScoredSortedSet(H5_ONLINE_USER_SET_KEY);
        scoredSortedSet.remove(userId);

        RMap<String, String> map = redissonClient.getMap(H5_ONLINE_USER_CONNECT_HASH);
        map.remove(userId);
    }

    @Override
    public boolean isUserAndroidOnline(String userId) {
        RScoredSortedSet<String> scoredSortedSet = redissonClient.getScoredSortedSet(ANDROID_ONLINE_USER_SET_KEY);
        return scoredSortedSet.contains(userId);
    }

    @Override
    public Long getAndroidOnlineUserCount() {
        RScoredSortedSet<String> scoredSortedSet = redissonClient.getScoredSortedSet(ANDROID_ONLINE_USER_SET_KEY);
        return scoredSortedSet.stream().count();
    }

    @Override
    public Long getH5OnlineUserCount() {
        RScoredSortedSet<String> scoredSortedSet = redissonClient.getScoredSortedSet(H5_ONLINE_USER_SET_KEY);
        return scoredSortedSet.stream().count();
    }

    @Override
    public List<String> getAndroidOnlineUserList() {
        RScoredSortedSet<String> scoredSortedSet = redissonClient.getScoredSortedSet(ANDROID_ONLINE_USER_SET_KEY);
        return scoredSortedSet.stream().distinct().toList();
    }

    @Override
    public List<String> getH5OnlineUserList() {
        RScoredSortedSet<String> scoredSortedSet = redissonClient.getScoredSortedSet(H5_ONLINE_USER_SET_KEY);
        return scoredSortedSet.stream().distinct().toList();
    }

    @Override
    public String getAndroidUserConnectServer(String userId) {
        RMap<String, String> map = redissonClient.getMap(ANDROID_ONLINE_USER_CONNECT_HASH);
        return map.get(userId);
    }

    @Override
    public Map<String, String> getAndroidConnectServerMap() {
        RMap<String, String> map = redissonClient.getMap(ANDROID_ONLINE_USER_CONNECT_HASH);
        return map.readAllMap();
    }

    @Override
    public Map<String, String> getH5ConnectServerMap() {
        RMap<String, String> map = redissonClient.getMap(H5_ONLINE_USER_CONNECT_HASH);
        return map.readAllMap();
    }

    @Override
    public String getH5UserConnectServer(String userId) {
        RMap<String, String> map = redissonClient.getMap(H5_ONLINE_USER_CONNECT_HASH);
        return map.get(userId);
    }
}
