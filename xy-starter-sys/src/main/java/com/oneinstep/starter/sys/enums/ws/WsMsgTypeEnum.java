package com.oneinstep.starter.sys.enums.ws;

import lombok.Getter;

/**
 * web socket 消息类型
 **/
@Getter
public enum WsMsgTypeEnum {
    DATA_CHANGED(1, "数据变更");

    private final int code;

    private final String desc;

    WsMsgTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static WsMsgTypeEnum of(int code) {
        for (WsMsgTypeEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
