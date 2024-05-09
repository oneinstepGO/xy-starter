package com.oneinstep.starter.core.error;

import lombok.Getter;

@Getter
public enum BaseCodeAndMsgError implements CodeAndMsgResponse {

    /**
     * 处理失败
     */
    FAILURE("-1", "请求失败"),

    /**
     * 处理成功
     */
    SUCCESS("100000", "处理成功"),

    /**
     * 请求方法不支持
     */
    METHOD_NOT_ALLOWED("100001", "请求方式不支持"),

    /**
     * 参数错误
     */
    ILLEGAL_ARGUMENT("100002", "参数错误"),

    /**
     * 访问频率过高
     */
    REQUEST_FREQUENCY_TOO_HIGH("100003", "访问频率过高"),

    /**
     * 获取锁失败
     */
    CAN_NOT_GET_LOCK("100004", "获取锁失败"),

    /**
     * http 接口调用失败
     */
    HTTP_CALL_FAIL("500014", " http 接口调用失败");

    private final String code;

    private final String message;

    BaseCodeAndMsgError(String code, String msg) {
        this.code = code;
        this.message = msg;
    }

}