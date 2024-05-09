package com.oneinstep.starter.security.interceptor;

import com.oneinstep.starter.core.exception.OneBaseException;
import com.oneinstep.starter.security.bean.bo.TokenUserInfoBO;
import com.oneinstep.starter.security.bean.dto.req.LoginReqDTO;

/*
 * 登录拦截器
 */
public interface LoginInterceptor {

    /**
     * 是否可以访问
     *
     * @param tokenUserInfoBO 账户信息
     * @throws OneBaseException 异常
     */
    void canAccess(LoginReqDTO loginReqDTO, TokenUserInfoBO tokenUserInfoBO) throws OneBaseException;
}
