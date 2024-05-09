package com.oneinstep.starter.sys.option;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.oneinstep.starter.core.dao.PageOption;
import com.oneinstep.starter.sys.bean.domain.SysUser;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 管理员查询参数
 **/
@ToString
@Getter
@Setter
public class QueryAdminUserOption extends PageOption {
    /**
     * 账户id
     */
    private Long userId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 昵称
     */
    private String nickName;
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
     */
    private Integer status;

    public QueryWrapper<SysUser> toQueryWrapper() {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(this.getUserId() != null, "user_id", userId);
        queryWrapper.eq(this.getUsername() != null, "username", this.getUsername());
        queryWrapper.like(this.getNickName() != null, "nick_name", this.getNickName());
        queryWrapper.eq(this.getLastLoginIp() != null, "last_login_ip", this.getLastLoginIp());
        queryWrapper.like(this.getRemark() != null, "remark", this.getRemark());
        queryWrapper.eq(this.getStatus() != null, "status", this.getStatus());

        return queryWrapper;
    }
}
