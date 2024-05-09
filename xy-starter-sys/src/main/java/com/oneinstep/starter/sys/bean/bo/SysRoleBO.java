package com.oneinstep.starter.sys.bean.bo;

import com.oneinstep.starter.sys.bean.domain.SysRole;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 角色
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysRoleBO extends SysRole {

    /**
     * 创建者ID
     */
    private String creator;
    /**
     * 菜单ID列表
     */
    private List<Long> menuIdList;
}
