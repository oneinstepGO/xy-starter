package com.oneinstep.starter.core.error;

import lombok.Getter;

@Getter
public enum SecurityCodeAndMsgError implements CodeAndMsgResponse {

    /**
     * 账户不存在
     */
    ACCOUNT_NOT_EXIST("500001", "账户不存在"),
    /**
     * 未登录
     */
    NOT_LOGIN("500002", "未登录"),
    /**
     * 用户名密码错误
     */
    WRONG_PASSWORD("500003", "用户名密码错误"),
    /**
     * 未授权
     */
    UNAUTHORIZED("500004", "未授权"),
    /**
     * 认证信息已过期
     */
    AUTH_INFO_IS_EXPIRED("500005", "认证信息已过期"),
    /**
     * 密码错误次数超过限制
     */
    WRONG_PASSWORD_TIMES_OVER_LIMIT("500007", "密码错误次数超过限制"),
    /**
     * 超级管理员账号不允许删除
     */
    SUPER_ADMIN_ACCOUNT_CANNOT_BE_DELETED("500008", "超级管理员账号不允许删除"),
    /**
     * 修改密码时，原密码不正确
     */
    OLD_PASSWORD_IS_WRONG_WHEN_CHANGE_PASSWORD("500009", "修改密码时，原密码不正确"),
    /**
     * 新密码不能与旧密码相同
     */
    NEW_PASSWORD_CAN_NOT_BE_THE_SAME_AS_THE_OLD_PASSWORD("500010", "新密码不能与旧密码相同"),
    /**
     * 不能修改超级管理员密码
     */
    CAN_NOT_CHANGE_THE_SUPER_ADMIN_PASSWORD("500011", "不能修改超级管理员密码"),
    /**
     * 登录IP不在白名单中
     */
    LOGIN_IP_IS_NOT_IN_WHITE_LIST("500012", "登录IP不在白名单中"),
    /**
     * 账户被禁用
     */
    ACCOUNT_IS_DISABLE("500013", "账户被禁用"),
    /**
     * 谷歌验证码验证失败
     */
    WRONG_GOOGLE_CODE("500015", "谷歌验证码验证失败"),
    /**
     * 验证码已过期，请重新获取
     */
    EXPIRED_GOOGLE_CODE("500016", "验证码已过期，请刷新页面重新获取"),
    LOGIN_METHOD_IS_NULL("500017", "登录方式不能为空"),
    MOBILE_AND_EMAIL_ARE_BLANK("500018", "手机号和邮箱不能同时为空"),
    VERIFY_CODE_IS_BLANK("500019", "验证码不能为空"),
    ACCOUNT_IS_BLANK("500020", "账户不能为空"),
    NOT_VALID_EMAIL("500021", "邮箱格式不正确"),
    NOT_VALID_MOBILE("500022", "手机号格式不正确"),
    MOBILE_IS_BLANK("500023", "手机号不能为空"),
    EMAIL_IS_BLANK("500024", "邮箱不能为空"),
    USERNAME_IS_BLANK("500025", "用户名不能为空");

    private final String code;

    private final String message;

    SecurityCodeAndMsgError(String code, String msg) {
        this.code = code;
        this.message = msg;
    }

}