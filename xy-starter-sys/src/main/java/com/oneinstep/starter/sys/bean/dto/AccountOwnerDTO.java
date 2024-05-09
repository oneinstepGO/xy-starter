package com.oneinstep.starter.sys.bean.dto;

import com.oneinstep.starter.security.constant.SystemType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 账户基础信息
 **/
@ToString
@Getter
@Setter
public class AccountOwnerDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
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
     * 登录白名单，用英文逗号分隔，不设置表示允许所有
     */
    private String loginWhiteList;
    /**
     * 创建者ID
     */
    private Long creatorId;
    /**
     * 创建者
     */
    private String creator;
    /**
     * 备注
     */
    private String remark;
    /**
     * 状态 0 禁用 1 正常
     */
    private Integer status;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
    /**
     * 角色ID列表
     */
    private List<Long> roleIds;
    /**
     * 角色名称列表
     */
    private List<String> roleNames;
    /**
     * 权限列表
     */
    private List<String> perms;
    /**
     * 系统类型
     */
    private SystemType systemType;
    /**
     * 是否已经绑定谷歌验证器
     */
    private boolean alreadyBindGoogleAuth;

    /**
     * 登陆账号ID
     */
    private String accountId;
}
