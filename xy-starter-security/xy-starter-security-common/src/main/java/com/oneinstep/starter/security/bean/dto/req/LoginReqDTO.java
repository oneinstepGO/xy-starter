package com.oneinstep.starter.security.bean.dto.req;

import com.oneinstep.starter.security.constant.LoginMethodEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
public class LoginReqDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 手机号
     */
    private String mobile;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 验证码
     */
    private String verifyCode;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;

    /**
     * 登录IP
     */
    private String loginIp;
    /**
     * 谷歌验证码
     */
    private String googleCode;
    /**
     * 登录方式
     */
    private LoginMethodEnum loginMethod;
}
