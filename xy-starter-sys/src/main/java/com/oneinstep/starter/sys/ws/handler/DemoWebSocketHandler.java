package com.oneinstep.starter.sys.ws.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.oneinstep.starter.core.utils.IPUtil;
import com.oneinstep.starter.core.utils.UriUtil;
import com.oneinstep.starter.sys.constants.WsConstant;
import com.oneinstep.starter.sys.enums.ws.WsClientMsgTypeEnum;
import com.oneinstep.starter.sys.service.WsOnlineUserService;
import com.oneinstep.starter.sys.ws.LocalWsUserSessionHolder;
import com.oneinstep.starter.sys.ws.dto.req.ClientEventDTO;
import com.oneinstep.starter.sys.ws.dto.req.WsClientReqMsg;
import com.oneinstep.starter.sys.ws.dto.resp.NormalResp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;


@Component
@Slf4j
public class DemoWebSocketHandler implements WebSocketHandler {

    @Resource(name = "commonThreadPool")
    private ExecutorService executorService;
    @Resource
    private WsOnlineUserService wsOnlineUserService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("建立连接>>>>>>> uri: {}, attributes: {}, sessionId={} ", session.getUri(), JSON.toJSONString(session.getAttributes()), session.getId());
        URI uri = session.getUri();

        if (uri != null && uri.getQuery() != null) {
            Map<String, String> parameters = UriUtil.getQueryMap(uri.getQuery());
            String reconnect = parameters.get("reconnect");
            String userId = parameters.get("userId");
            String client = parameters.get("client");
            if (StringUtils.isNotBlank(client) && "h5".equalsIgnoreCase(client) && StringUtils.isNotBlank(userId)) {
                session.getAttributes().put(WsConstant.USER_ID, userId);
                session.getAttributes().put(WsConstant.CLIENT, "h5");

                log.info("H5客户端连接成功......sessionId={}, userId={}", session.getId(), userId);
                wsOnlineUserService.addH5OnlineUser(userId, session);

                return;
            }

            if (StringUtils.equals("1", reconnect)) {
                log.info("重连成功......sessionId={}, userId={}", session.getId(), userId);
                if (StringUtils.isNotBlank(userId)) {

                    session.getAttributes().put(WsConstant.USER_ID, userId);
                    wsOnlineUserService.addAndroidOnlineUser(userId, session);
                    return;
                }
            }
        }
        // 将session放入集合中
        LocalWsUserSessionHolder.putUserNotLoginSession(session.getId(), session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

        try {
            handleReceiveMsg(session, message);
        } catch (Exception e) {
            log.error("处理消息异常", e);
        }

    }

    private void handleReceiveMsg(WebSocketSession session, WebSocketMessage<?> message) throws IOException {
        if (message instanceof PongMessage) {
            return;
        }
        if (message instanceof PingMessage) {
            if (session.isOpen()) {
                session.sendMessage(new PongMessage());
            }
            return;
        }
        Object payload = message.getPayload();
        // 处理接收到的消息
        log.info("收到消息: sessionId={}, message = {}", session.getId(), payload);

        if (!JSON.isValid(String.valueOf(payload))) {
            log.info("收到非JSON消息:{}", payload);
            if (session.isOpen()) {
                session.sendMessage(new TextMessage("已收到消息，非json。=> [" + payload + "]"));
            }
            return;
        }

        // 将 string 转为 json 对象
        WsClientReqMsg<?> clientReqMsg = null;
        try {
            clientReqMsg = JSONObject.parseObject(payload.toString(), WsClientReqMsg.class);
        } catch (Exception e) {
            log.error("转换json对象失败", e);
            if (session.isOpen()) {
                session.sendMessage(new TextMessage("非法JSON。=> [" + payload + "]"));
            }
        }

        if (clientReqMsg == null) {
            log.info("收到JSON消息，但转换为WsClientReqMsg对象失败。 {}", payload);
            if (session.isOpen()) {
                session.sendMessage(new TextMessage("消息格式错误。=> [" + payload + "]"));
            }
            return;
        }

        WsClientMsgTypeEnum actionEnum = checkClientMsg(session, clientReqMsg, payload);
        if (actionEnum == null) return;

        if (WsClientMsgTypeEnum.SEND_EVENT == actionEnum) {

            JSONObject jsonObject = (JSONObject) clientReqMsg.getData();
            ClientEventDTO data = jsonObject.toJavaObject(ClientEventDTO.class);

            Integer eventType = data.getEventType();

            EventTypeEnum eventTypeEnum = EventTypeEnum.of(eventType);
            if (eventTypeEnum == null) {
                // 如果是其他事件
                sendMessage(session, "消息eventType无法识别。=> [" + payload + "]");
                return;
            }
            // 如果是登陆事件
            if (eventTypeEnum == EventTypeEnum.LOGIN) {

                // 进入大厅
                Long userId = clientReqMsg.getUserId();

                // 将用户id和session绑定
                handleLogin(session, String.valueOf(userId));

                return;
            } else {
                // 如果是其他事件
                sendMessage(session, "消息eventType暂时未处理。=> [" + payload + "]");
                return;
            }
        }


        if (session.isOpen()) {
            session.sendMessage(new TextMessage("已收到消息。=> [" + payload + "]"));
        }
    }

    private static WsClientMsgTypeEnum checkClientMsg(WebSocketSession session, WsClientReqMsg<?> clientReqMsg, Object payload) throws IOException {

        if (clientReqMsg.getData() == null) {
            log.info("收到JSON消息，但数据data为空。 {}", payload);
            if (session.isOpen()) {
                session.sendMessage(new TextMessage("消息缺失data元素: " + payload));
            }
            return null;
        }

        String action = clientReqMsg.getAction();
        if (StringUtils.isBlank(action)) {
            log.info("收到JSON消息，但action为空。 {}", payload);
            if (session.isOpen()) {
                session.sendMessage(new TextMessage("消息缺失action元素: " + payload));
            }
            return null;
        }

        WsClientMsgTypeEnum actionEnum = WsClientMsgTypeEnum.of(action);

        if (actionEnum == null) {
            // 如果是其他事件
            if (session.isOpen()) {
                session.sendMessage(new TextMessage("消息action无法识别: " + payload));
            }
            return null;
        }
        return actionEnum;
    }

    private void handleLogin(WebSocketSession session, String userId) {

        Object oldUserIdObj = session.getAttributes().get(WsConstant.USER_ID);
        if (oldUserIdObj != null) {
            String oldUserId = String.valueOf(oldUserIdObj).trim();
            // 如果不是同一个用户，将旧用户踢下线
            if (!StringUtils.equals(oldUserId, userId)) {
                log.info("用户:{} 登录，但已有用户:{} 登录，将旧用户踢下线", userId, oldUserId);
                wsOnlineUserService.removeAndroidOnlineUser(oldUserId);
            }
        }

        session.getAttributes().put(WsConstant.USER_ID, userId);

        wsOnlineUserService.addAndroidOnlineUser(userId, session);
        // 移除 旧 session
        LocalWsUserSessionHolder.removeUserNotLoginSession(session.getId());

        log.info("用户:{} 登录成功", userId);

        NormalResp result = new NormalResp();
        result.setCode(NormalResp.SUCCESS);
        result.setMsg("登录成功");
        result.setTimestamp(System.currentTimeMillis());

        sendMessage(session, JSON.toJSONString(result));

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("传输异常: sessionId={}", session.getId(), exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

        // 将session从集合中移除
        Object userIdObj = session.getAttributes().get(WsConstant.USER_ID);

        log.info("连接关闭: sessionId={}, userId={}, status={}", session.getId(), userIdObj, JSON.toJSONString(closeStatus));
        if (userIdObj != null) {
            String userId = String.valueOf(userIdObj);
            Object clientObj = session.getAttributes().get(WsConstant.CLIENT);
            if (clientObj != null && "h5".equalsIgnoreCase(String.valueOf(clientObj))) {

                log.info("H5客户端连接关闭......sessionId={}, userId={}", session.getId(), userId);
                WebSocketSession remove = LocalWsUserSessionHolder.removeH5LoginUserSession(userId);
                if (remove != null) {
                    remove.close();
                }
                wsOnlineUserService.removeH5OnlineUser(userId);

            } else {
                WebSocketSession remove = LocalWsUserSessionHolder.removeAndroidLoginUserSession(userId);
                if (remove != null) {
                    remove.close();
                }
                wsOnlineUserService.removeAndroidOnlineUser(userId);
            }
            return;
        }
        WebSocketSession remove = LocalWsUserSessionHolder.removeUserNotLoginSession(session.getId());
        if (remove != null) {
            remove.close();
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
    // 实现其他必要的方法

    public void sendMessage(WebSocketSession session, String message) {
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));

            } catch (IOException e) {
                log.error("发送消息失败", e);
            }
        }
    }

    public void clearClosedSession() {

        List<String> h5OnlineUserIds = LocalWsUserSessionHolder.getH5OnlineUserIds();
        if (CollectionUtils.isNotEmpty(h5OnlineUserIds)) {
            h5OnlineUserIds.forEach(userId -> executorService.execute(() -> {
                WebSocketSession session = LocalWsUserSessionHolder.getH5LoginUserSession(userId);
                if (session == null || !session.isOpen()) {
                    log.info("本机用户:{} h5在线，但session已关闭，清理本地session", userId);
                    LocalWsUserSessionHolder.removeH5LoginUserSession(userId);
                }
            }));
        }

        List<String> h5OnlineUserList = wsOnlineUserService.getH5OnlineUserList();
        if (CollectionUtils.isNotEmpty(h5OnlineUserList)) {
            h5OnlineUserList.forEach(userId -> executorService.execute(() -> {

                String h5UserConnectServer = wsOnlineUserService.getH5UserConnectServer(userId);
                if (IPUtil.getLocalIPAddress().equals(h5UserConnectServer)) {
                    WebSocketSession session = LocalWsUserSessionHolder.getH5LoginUserSession(userId);
                    if (session == null || !session.isOpen()) {
                        log.info("redis 中本机用户:{} h5在线，但session已关闭，清理redis key", userId);
                        LocalWsUserSessionHolder.removeH5LoginUserSession(userId);
                        wsOnlineUserService.removeH5OnlineUser(userId);
                    }
                }
            }));
        }

        List<String> androidOnlineUserIds = LocalWsUserSessionHolder.getAndroidOnlineUserIds();
        if (CollectionUtils.isNotEmpty(androidOnlineUserIds)) {
            androidOnlineUserIds.forEach(userId -> executorService.execute(() -> {
                WebSocketSession session = LocalWsUserSessionHolder.getAndroidLoginUserSession(userId);
                if (session == null || !session.isOpen()) {
                    log.info("本机用户:{} android 在线，但session已关闭，清理本地session", userId);
                    LocalWsUserSessionHolder.removeAndroidLoginUserSession(userId);
                }
            }));
        }

        List<String> androidOnlineUserList = wsOnlineUserService.getAndroidOnlineUserList();
        if (CollectionUtils.isNotEmpty(androidOnlineUserList)) {
            androidOnlineUserList.forEach(userId -> executorService.execute(() -> {

                String androidUserConnectServer = wsOnlineUserService.getAndroidUserConnectServer(userId);
                if (IPUtil.getLocalIPAddress().equals(androidUserConnectServer)) {
                    WebSocketSession session = LocalWsUserSessionHolder.getAndroidLoginUserSession(userId);
                    if (session == null || !session.isOpen()) {
                        log.info("redis 中本机用户:{} android 在线，但session已关闭，清理redis key", userId);
                        LocalWsUserSessionHolder.removeAndroidLoginUserSession(userId);
                        wsOnlineUserService.removeAndroidOnlineUser(userId);
                    }
                }
            }));
        }

    }

}
