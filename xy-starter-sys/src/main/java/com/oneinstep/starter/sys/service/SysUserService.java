package com.oneinstep.starter.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oneinstep.starter.sys.bean.bo.SysUserBO;
import com.oneinstep.starter.sys.bean.domain.SysUser;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 管理员服务
 **/
public interface SysUserService extends IService<SysUser> {

    String test(Integer order);

    /**
     * 更新管理员
     *
     * @param adminUserBO 管理员信息
     * @return 是否成功
     */
    boolean updateUser(@NotNull SysUserBO adminUserBO);

    /**
     * 根据id查询管理员
     *
     * @param userId 管理员id
     * @return 是否成功
     */
    boolean deleteUser(@NotNull Long userId);

    /**
     * 保存管理员
     *
     * @param adminUserBO 管理员信息
     * @return 是否成功
     */
    boolean saveUser(@NotNull SysUserBO adminUserBO);

    /**
     * 根据用户id查询角色id
     *
     * @param userId 用户id
     * @return 角色id列表
     */
    List<Long> getRoleIdsByUserId(Long userId);

    /**
     * 根据用户id查询权限
     *
     * @param userId 用户id
     * @return 权限列表
     */
    List<String> getPermsByUserId(Long userId);
}
