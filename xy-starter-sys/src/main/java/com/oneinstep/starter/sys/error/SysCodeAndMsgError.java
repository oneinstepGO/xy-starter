package com.oneinstep.starter.sys.error;

import com.oneinstep.starter.core.error.CodeAndMsgResponse;
import lombok.Getter;

@Getter
public enum SysCodeAndMsgError implements CodeAndMsgResponse {

    /**
     * 保存或者更新用户时 角色不能为空
     */
    ACCOUNT_OWNER_ROLE_IS_EMPTY("600002", "保存或者更新用户时 角色不能为空");

    private final String code;

    private final String message;

    SysCodeAndMsgError(String code, String msg) {
        this.code = code;
        this.message = msg;
    }

}