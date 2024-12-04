package com.oneinstep.starter.sys.enums.ws;

import lombok.Getter;

/**
 * web socket 消息类型
 **/
@Getter
public enum DataTypeEnum {

    COMMON_DATA(1, "普通数据"),
    ;

    private final Integer code;

    private final String desc;

    DataTypeEnum(final Integer code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static DataTypeEnum of(Integer code) {
        for (DataTypeEnum typeEnum : DataTypeEnum.values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }


}
