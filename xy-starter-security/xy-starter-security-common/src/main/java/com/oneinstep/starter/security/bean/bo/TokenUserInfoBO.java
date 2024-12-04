package com.oneinstep.starter.security.bean.bo;

import com.oneinstep.starter.security.constant.SecurityConstants;
import com.oneinstep.starter.security.constant.SystemType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 账户
 **/
@Getter
@Setter
@ToString
public class TokenUserInfoBO implements UserDetails, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 登录密码
     */
    private String password;
    /*
     * 登陆时 google 密钥
     */
    private String googleSecret;
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
     * 权限列表
     */
    private List<String> perms;
    /**
     * 系统类型
     */
    private SystemType systemType;
    /**
     * 登陆账号ID
     */
    private String accountId;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        Set<GrantedAuthority> authorities = roleIds.stream()
                .map(roleId -> new SimpleGrantedAuthority(SecurityConstants.ROLE_PREFIX + roleId))
                .collect(Collectors.toSet());

        this.getPerms()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .forEach(authorities::add);

        return authorities;
    }

    @Override
    public String getUsername() {
        return getAccountId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
