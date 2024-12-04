package com.oneinstep.starter.core.exception;


import com.oneinstep.starter.core.error.BaseCodeAndMsgError;
import com.oneinstep.starter.core.error.CodeAndMsgResponse;

/**
 * 通用异常
 */
public class OneBaseException extends RuntimeException implements CodeAndMsgResponse {

    private final String code;

    private final String message;

    public OneBaseException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public OneBaseException(CodeAndMsgResponse codeAndMsgResponse) {
        super(codeAndMsgResponse.getMessage());
        this.code = codeAndMsgResponse.getCode();
        this.message = codeAndMsgResponse.getMessage();
    }

    public OneBaseException(String message) {
        super(message);
        this.code = BaseCodeAndMsgError.FAILURE.getCode();
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

}
