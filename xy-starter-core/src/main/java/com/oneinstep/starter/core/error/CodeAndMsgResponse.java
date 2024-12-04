package com.oneinstep.starter.core.error;

import com.oneinstep.starter.core.exception.OneBaseException;

public interface CodeAndMsgResponse {
    /**
     * 获取响应码
     *
     * @return 响应码
     */
    String getCode();

    /**
     * 获取响应信息
     *
     * @return 响应信息
     */
    String getMessage();

    /**
     * 转换为 OneBaseException 异常
     *
     * @return OneBaseException
     */
    default OneBaseException toOneBaseException() {
        return new OneBaseException(this);
    }

    /**
     * 转换为 OneBaseException 异常
     *
     * @param newMsg 新的异常信息
     * @return OneBaseException
     */
    default OneBaseException toOneBaseException(String newMsg) {
        return new OneBaseException(this.getCode(), newMsg);
    }

}