package com.oneinstep.starter.sys.ws.handler;

import lombok.Getter;

@Getter
public enum EventTypeEnum {
    LOGIN(0, "登录");
    private final Integer code;
    private final String desc;

    EventTypeEnum(final Integer code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static EventTypeEnum of(Integer eventType) {
        for (EventTypeEnum value : values()) {
            if (value.ordinal() == eventType) {
                return value;
            }
        }
        return null;
    }
}
