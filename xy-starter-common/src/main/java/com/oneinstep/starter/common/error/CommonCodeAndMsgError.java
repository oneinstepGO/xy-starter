package com.oneinstep.starter.common.error;

import com.oneinstep.starter.core.error.CodeAndMsgResponse;
import lombok.Getter;

@Getter
public enum CommonCodeAndMsgError implements CodeAndMsgResponse {

    /**
     * 查询资源不存在
     */
    QUERY_RESOURCE_NOT_EXIST("500006", "查询资源不存在"),

    /**
     * 添加资源已存在
     */
    ADD_RESOURCE_ALREADY_EXIST("500006", "添加资源已存在");

    private final String code;

    private final String message;

    CommonCodeAndMsgError(String code, String msg) {
        this.code = code;
        this.message = msg;
    }

}