package com.oneinstep.starter.sys.enums.ws;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * web socket 消息类型
 **/
@Getter
public enum WsClientMsgTypeEnum {

    /**
     * 发送事件
     */
    SEND_EVENT("SEND_EVENT", "发送事件"),
    /**
     * 消息确认
     */
    ACK_MSG("ACK_MSG", "消息确认");

    private final String code;

    private final String desc;

    WsClientMsgTypeEnum(final String code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static WsClientMsgTypeEnum of(String code) {
        for (WsClientMsgTypeEnum typeEnum : WsClientMsgTypeEnum.values()) {
            if (StringUtils.equals(code, typeEnum.getCode())) {
                return typeEnum;
            }
        }
        return null;
    }


}
