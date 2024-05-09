package com.oneinstep.starter.security.exception;

import com.oneinstep.starter.core.error.SecurityCodeAndMsgError;
import com.oneinstep.starter.core.exception.OneBaseException;

/**
 * 账户 不存在异常
 */
public class AccountNotExistException extends OneBaseException {

    public AccountNotExistException() {
        super(SecurityCodeAndMsgError.ACCOUNT_NOT_EXIST);
    }

}
