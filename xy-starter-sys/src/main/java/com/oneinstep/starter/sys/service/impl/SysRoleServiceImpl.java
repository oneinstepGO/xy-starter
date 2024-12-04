package com.oneinstep.starter.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oneinstep.starter.common.error.CommonCodeAndMsgError;
import com.oneinstep.starter.core.constants.CommonStatus;
import com.oneinstep.starter.core.utils.BeanCopyUtils;
import com.oneinstep.starter.sys.bean.bo.SysRoleBO;
import com.oneinstep.starter.sys.bean.domain.SysRole;
import com.oneinstep.starter.sys.bean.domain.SysRoleMenu;
import com.oneinstep.starter.sys.mapper.SysRoleMapper;
import com.oneinstep.starter.sys.service.SysRoleMenuService;
import com.oneinstep.starter.sys.service.SysRoleService;
import com.oneinstep.starter.sys.service.SysUserRoleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;


@Service
@Slf4j
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Resource
    private SysRoleMenuService sysRoleMenuService;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private SysUserRoleService sysUserRoleService;

    @Override
    public List<SysRoleBO> listAllRoleIdAndNames() {
        List<SysRole> roles = this.lambdaQuery().list();
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        return roles.stream().map(r -> BeanCopyUtils.copy(r, SysRoleBO.class)).toList();
    }

    @Override
    public boolean saveRoleAndRoleMenu(SysRoleBO role) {

        SysRole old = this.lambdaQuery().eq(SysRole::getRoleName, role.getRoleName()).one();

        if (old != null) {
            throw CommonCodeAndMsgError.ADD_RESOURCE_ALREADY_EXIST.toOneBaseException("角色名称已存在");
        }
        return Boolean.TRUE.equals(transactionTemplate.execute(status -> {
            boolean saveRoleResult = this.saveRole(role);
            if (!saveRoleResult) {
                status.setRollbackOnly();
                return false;
            }

            Boolean x = saveRoleAndMenus(role, status);
            if (x != null) return x;

            return true;
        }));

    }

    private @Nullable Boolean saveRoleAndMenus(SysRoleBO role, TransactionStatus status) {
        if (CollectionUtils.isEmpty(role.getMenuIdList())) {
            log.info("角色id为{}的角色没有菜单", role.getId());
            return true;
        }
        List<Long> menuIdList = role.getMenuIdList().stream().distinct().toList();
        //保存角色与菜单关系
        for (Long menuId : menuIdList) {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(role.getId());
            sysRoleMenu.setMenuId(menuId);
            boolean saveRoleMenuResult = sysRoleMenuService.save(sysRoleMenu);
            if (!saveRoleMenuResult) {
                status.setRollbackOnly();
                log.error("保存角色与菜单关系失败, 角色id为{}", role.getId());
                return false;
            }
        }
        return null;
    }

    @Override
    public boolean updateRoleAndRoleMenu(SysRoleBO role) {
        Long roleId = role.getId();

        return Boolean.TRUE.equals(transactionTemplate.execute(status -> {
            // 更新角色
            boolean updateRoleResult = this.updateRole(role);
            if (!updateRoleResult) {
                log.error("更新角色失败, 角色id为{}", roleId);
                status.setRollbackOnly();
                return false;
            }

            //先删除 旧的角色与菜单关系
            boolean remove = sysRoleMenuService.remove(new LambdaQueryWrapper<>(SysRoleMenu.class).eq(SysRoleMenu::getRoleId, roleId));
            if (!remove) {
                log.error("删除角色与菜单关系失败, 角色id为{}", roleId);
                status.setRollbackOnly();
                return false;
            }

            Boolean x = saveRoleAndMenus(role, status);
            if (x != null) return x;

            return true;

        }));

    }

    @Override
    public SysRoleBO queryRoleInfoIncludeMenusAndPerms(Long roleId) {
        SysRole sysRole = this.getById(roleId);
        if (sysRole == null) {
            log.error("角色id为{}的角色不存在", roleId);
            throw CommonCodeAndMsgError.QUERY_RESOURCE_NOT_EXIST.toOneBaseException("角色不存在");
        }
        SysRoleBO sysRoleBO = BeanCopyUtils.copy(sysRole, SysRoleBO.class);
        List<SysRoleMenu> sysMenus = sysRoleMenuService.lambdaQuery().eq(SysRoleMenu::getRoleId, roleId).list();
        if (CollectionUtils.isEmpty(sysMenus)) {
            log.info("角色id为{}的角色没有菜单和权限", roleId);
            sysRoleBO.setMenuIdList(Collections.emptyList());
            return sysRoleBO;
        }
        List<Long> menuIdList = sysMenus.stream().map(SysRoleMenu::getMenuId).distinct().toList();
        sysRoleBO.setMenuIdList(menuIdList);
        return sysRoleBO;
    }

    private boolean updateRole(SysRoleBO sysRoleBO) {
        SysRole sysRole = BeanCopyUtils.copy(sysRoleBO, SysRole.class);
        LocalDateTime now = LocalDateTime.now();
        sysRole.setUpdateTime(now);
        return this.updateById(sysRole);
    }

    private boolean saveRole(SysRoleBO sysRoleBO) {
        LocalDateTime now = LocalDateTime.now();
        sysRoleBO.setCreateTime(now);
        sysRoleBO.setUpdateTime(now);
        sysRoleBO.setStatus(CommonStatus.NORMAL.getCode());
        SysRole sysRole = BeanCopyUtils.copy(sysRoleBO, SysRole.class);
        boolean ret = this.save(sysRole);
        if (ret) {
            sysRoleBO.setId(sysRole.getId());
            return true;
        } else {
            return false;
        }
    }

}
