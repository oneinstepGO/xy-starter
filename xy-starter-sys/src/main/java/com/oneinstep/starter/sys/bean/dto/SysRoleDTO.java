package com.oneinstep.starter.sys.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色
 **/
@Getter
@Setter
@ToString
@Schema(description = "角色信息")
public class SysRoleDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色id
     */
    @Schema(description = "角色id")
    private Long id;
    /**
     * 角色名称
     */
    @Schema(description = "角色名称")
    private String roleName;
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
    /**
     * 状态 0-禁用 1-启用
     */
    @Schema(description = "状态 0-禁用 1-启用")
    private Integer status;
    /**
     * 创建者
     */
    @Schema(description = "创建者")
    private String creator;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    /**
     * 菜单、权限 ID列表
     */
    @Schema(description = "菜单、权限 ID列表")
    private List<Long> menuIdList;

    /**
     * 菜单及权限列表
     */
    @Schema(description = "菜单及权限列表")
    private List<SysMenuDTO> menuAndPerms;

}
