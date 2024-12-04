package com.oneinstep.starter.sys.bean.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.oneinstep.starter.core.constants.CommonStatus;
import com.oneinstep.starter.core.excel.ExcelCell;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统管理员
 */
@Data
@TableName("t_sys_user")
public class SysUser {
    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    @ExcelCell(0)
    private Long userId;
    /**
     * 用户名
     */
    @ExcelCell(1)
    private String username;
    /**
     * 登录密码
     */
    private String password;
    /**
     * 登陆时 google 密钥
     */
    private String googleSecret;
    /**
     * 昵称
     */
    @ExcelCell(2)
    private String nickName;
    /**
     * 最后登录时间
     */
    @ExcelCell(value = 3, isLocalDateTime = true)
    private LocalDateTime lastLoginTime;
    /**
     * 最后登录IP
     */
    @ExcelCell(4)
    private String lastLoginIp;
    /**
     * 登录白名单，用英文逗号分隔，不设置表示允许所有
     */
    @ExcelCell(5)
    private String loginWhiteList;
    /**
     * 创建者ID
     */
    @ExcelCell(6)
    private Long creatorId;
    /**
     * 备注
     */
    @ExcelCell(7)
    private String remark;
    /**
     * 状态 0 禁用 1 正常
     *
     * @see CommonStatus
     */
    @ExcelCell(value = 8, isEnum = true, enumClass = CommonStatus.class)
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
    @ExcelCell(value = 9, isLocalDateTime = true)
    private LocalDateTime createTime;
    /**
     * 修改时间
     */
    @ExcelCell(value = 10, isLocalDateTime = true)
    private LocalDateTime updateTime;
}
