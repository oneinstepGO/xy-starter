package com.oneinstep.starter.business.api.bean.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.oneinstep.starter.core.constants.CommonStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户
 */
@Getter
@Setter
@ToString
@TableName("t_user")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.INPUT)
    private Long userId;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 真实姓名
     */
    private String realName;
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
    private String mobile;
    /**
     * 微信开放平台ID
     */
    private String wxOpenId;
    /**
     * 登录密码
     */
    private String loginPassword;
    /**
     * 支付密码
     */
    private String payPassword;
    /**
     * 登陆时 google 密钥
     */
    private String googleSecret;
    /**
     * 注册时间
     */
    private LocalDateTime regTime;
    /**
     * 注册IP
     */
    private String regIp;
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    /**
     * 最后登录IP
     */
    private String lastLoginIp;
    /**
     * 备注
     */
    private String remark;
    /**
     * 状态 0 禁用 1 正常
     *
     * @see CommonStatus
     */
    private Integer status;
    /**
     * 删除标识 0 未删除 1 已删除
     */
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
    /**
     * 版本号
     */
    @Version
    private Long version;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}
