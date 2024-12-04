package com.oneinstep.starter.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oneinstep.starter.sys.bean.bo.SysRoleBO;
import com.oneinstep.starter.sys.bean.domain.SysRole;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 角色
 */
public interface SysRoleService extends IService<SysRole> {
    /**
     * 查询 所有角色id 和 名称列表，用于给用户分配角色
     *
     * @return 查询 所有角色id 和 名称列表，用于给用户分配角色
     */
    List<SysRoleBO> listAllRoleIdAndNames();

    /**
     * 保存角色 与 角色菜单关系
     *
     * @param role 角色
     */
    boolean saveRoleAndRoleMenu(SysRoleBO role);

    /**
     * 更新角色 与 角色菜单关系
     *
     * @param role 角色
     */
    boolean updateRoleAndRoleMenu(SysRoleBO role);

    /**
     * 根据角色id 获取菜单及权限列表
     *
     * @param roleId 角色id
     * @return 菜单及权限列表
     */
    SysRoleBO queryRoleInfoIncludeMenusAndPerms(@NotNull Long roleId);
}
