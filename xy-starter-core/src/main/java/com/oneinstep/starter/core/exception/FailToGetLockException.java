package com.oneinstep.starter.core.exception;

import static com.oneinstep.starter.core.error.BaseCodeAndMsgError.CAN_NOT_GET_LOCK;

/**
 * 获取分布式锁失败异常
 **/
public class FailToGetLockException extends OneBaseException {

    public FailToGetLockException() {
        super(CAN_NOT_GET_LOCK);
    }

}
