package com.oneinstep.starter.core.exception;

import com.oneinstep.starter.core.error.BaseCodeAndMsgError;

/**
 * Http 接口调用失败异常
 **/
public class HttpCallException extends OneBaseException {
    public HttpCallException() {
        super(BaseCodeAndMsgError.HTTP_CALL_FAIL);
    }
}
