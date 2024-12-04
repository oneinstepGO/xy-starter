package com.oneinstep.starter.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oneinstep.starter.sys.bean.domain.SysMenu;
import com.oneinstep.starter.sys.bean.dto.SysMenuDTO;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 菜单权限
 **/
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 通过账户id 获取 菜单列表
     *
     * @param userId 账户id
     * @return 菜单列表
     */
    List<SysMenuDTO> getMenuListByUserId(@NotNull Long userId);

    /**
     * 所有菜单及权限列表(用于新建、修改角色时 获取菜单的信息)
     *
     * @return 所有菜单及权限列表
     */
    List<SysMenuDTO> listAllMenuAndPerm();


    /**
     * 根据用户id 获取权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> queryPermsByUserId(@NotNull Long userId);

}
