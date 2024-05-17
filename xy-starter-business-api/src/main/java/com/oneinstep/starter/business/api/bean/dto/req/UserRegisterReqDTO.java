package com.oneinstep.starter.business.api.bean.dto.req;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@ToString
public class UserRegisterReqDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 昵称
     */
    private String nickName;
    /**
     * 性别
     */
    private Character gender;
    /**
     * 生日
     */
    private LocalDate birthday;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 手机号
     */
    @NotEmpty(message = "手机号不能为空")
    private String mobile;
    /**
     * 微信开放平台ID
     */
    private String wxOpenId;
    /**
     * 登录密码
     */
    @NotEmpty(message = "登录密码不能为空")
    private String loginPassword;
    /**
     * 手机验证码
     */
    @NotEmpty(message = "手机验证码不能为空")
    private String verifyCode;

    /**
     * 注册IP
     */
    private String regIp;
}
