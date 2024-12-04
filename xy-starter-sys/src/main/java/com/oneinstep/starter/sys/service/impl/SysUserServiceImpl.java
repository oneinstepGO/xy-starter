package com.oneinstep.starter.sys.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oneinstep.starter.common.error.CommonCodeAndMsgError;
import com.oneinstep.starter.core.constants.CommonConstant;
import com.oneinstep.starter.core.log.annotition.Logging;
import com.oneinstep.starter.core.redis.annotition.EnableDistributedLock;
import com.oneinstep.starter.core.redis.annotition.LockParam;
import com.oneinstep.starter.core.utils.BeanCopyUtils;
import com.oneinstep.starter.security.constant.SecurityConstants;
import com.oneinstep.starter.security.constant.SystemType;
import com.oneinstep.starter.security.service.CommonSessionService;
import com.oneinstep.starter.sys.bean.bo.SysUserBO;
import com.oneinstep.starter.sys.bean.domain.SysMenu;
import com.oneinstep.starter.sys.bean.domain.SysUser;
import com.oneinstep.starter.sys.bean.domain.SysUserRole;
import com.oneinstep.starter.sys.enums.MenuTypeEnum;
import com.oneinstep.starter.sys.error.SysCodeAndMsgError;
import com.oneinstep.starter.sys.mapper.SysUserMapper;
import com.oneinstep.starter.sys.service.SysMenuService;
import com.oneinstep.starter.sys.service.SysUserRoleService;
import com.oneinstep.starter.sys.service.SysUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 后台管理员用户
 **/
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private SysUserRoleService sysUserRoleService;
    @Resource
    private CommonSessionService commonSessionService;
    @Resource
    private SysMenuService sysMenuService;

    @Override
    @EnableDistributedLock(lockPrefix = "test:%d", lockTimeout = 10)
    public String test(@LockParam(0) Integer order) {
        log.info("test:{}", order);
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "test";
    }

    @Override
    @Logging(printArgs = true, printResult = true)
    public boolean updateUser(SysUserBO adminUserBO) {
        Long userId = adminUserBO.getUserId();

        SysUser oldUser = this.getById(userId);
        if (oldUser == null) {
            log.error("user not exist, userId:{}", userId);
            throw CommonCodeAndMsgError.QUERY_RESOURCE_NOT_EXIST.toOneBaseException("要更新的管理员不存在");
        }

        SysUser sysUser = BeanCopyUtils.copy(adminUserBO, SysUser.class);
        return Boolean.TRUE.equals(transactionTemplate.execute(status -> {
            boolean b = this.updateById(sysUser);
            if (!b) {
                status.setRollbackOnly();
                return false;
            }

            boolean remove = sysUserRoleService.remove(new LambdaQueryWrapper<>(SysUserRole.class)
                    .eq(SysUserRole::getSysUserId, userId));


            if (!remove) {
                log.error("delete sys user roles failed, userId:{}", userId);
                status.setRollbackOnly();
                return false;
            }

            // update account and role relation
            List<Long> roleIds = adminUserBO.getRoleIds();
            if (CollectionUtils.isEmpty(roleIds)) {
                log.error("roleIds is empty, adminUserBO:{}", JSON.toJSONString(adminUserBO));
                throw SysCodeAndMsgError.ACCOUNT_OWNER_ROLE_IS_EMPTY.toOneBaseException();
            }

            for (Long roleId : roleIds) {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setSysUserId(userId);
                sysUserRole.setRoleId(roleId);
                boolean save = sysUserRoleService.save(sysUserRole);
                if (!save) {
                    log.error("save sys user roles failed, userId:{}, roleId:{}", userId, roleId);
                    status.setRollbackOnly();
                    return false;
                }
            }

            return true;
        }));

    }

    @Override
    public boolean saveUser(SysUserBO sysUserBO) {
        String username = sysUserBO.getUsername();

        this.lambdaQuery().eq(SysUser::getUsername, username).oneOpt().ifPresent(sysUser -> {
            log.error("username is exist, username:{}", username);
            throw CommonCodeAndMsgError.ADD_RESOURCE_ALREADY_EXIST.toOneBaseException("用户名已存在");
        });

        return Boolean.TRUE.equals(transactionTemplate.execute(status -> {

                    SysUser sysUser = BeanCopyUtils.copy(sysUserBO, SysUser.class);
                    boolean saveUserRet = this.save(sysUser);
                    if (!saveUserRet) {
                        log.error("create user failed, sysUserBO:{}", JSON.toJSONString(sysUserBO));
                        status.setRollbackOnly();
                        return false;
                    }

                    Long userId = sysUser.getUserId();

                    List<Long> roleIds = sysUserBO.getRoleIds();

                    if (CollectionUtils.isEmpty(roleIds)) {
                        log.error("roleIds is empty, accountOwnerBO:{}", JSON.toJSONString(sysUserBO));
                        throw SysCodeAndMsgError.ACCOUNT_OWNER_ROLE_IS_EMPTY.toOneBaseException();
                    }


                    for (Long roleId : roleIds) {
                        SysUserRole sysUserRole = new SysUserRole();
                        sysUserRole.setSysUserId(userId);
                        sysUserRole.setRoleId(roleId);
                        boolean insert = sysUserRoleService.save(sysUserRole);
                        if (!insert) {
                            log.error("save account owner roles failed, userId:{}, roleId:{}", userId, roleId);
                            status.setRollbackOnly();
                            return false;
                        }
                    }

                    return true;
                })
        );
    }

    @Override
    @Logging(printArgs = true, printResult = true)
    public boolean deleteUser(Long userId) {
        SysUser sysUser = getById(userId);
        if (sysUser == null) {
            log.error("user not exist, userId:{}", userId);
            throw CommonCodeAndMsgError.QUERY_RESOURCE_NOT_EXIST.toOneBaseException("要删除的管理员不存在");
        }
        SysUserBO accountOwnerBO = BeanCopyUtils.copy(sysUser, SysUserBO.class);

        boolean ret = Boolean.TRUE.equals(transactionTemplate.execute(status -> {

            //delete user
            boolean deleteUserRet = this.remove(new LambdaQueryWrapper<>(SysUser.class).eq(SysUser::getUserId, userId));
            if (!deleteUserRet) {
                log.error("delete sys user failed, accountOwnerBO:{}", JSON.toJSONString(accountOwnerBO));
                status.setRollbackOnly();
                return false;
            }

            // delete sysUser and role relation
            boolean remove = sysUserRoleService.remove(new LambdaQueryWrapper<>(SysUserRole.class).eq(SysUserRole::getSysUserId, userId));
            if (!remove) {
                log.error("delete sys user roles failed, userId:{}", userId);
                status.setRollbackOnly();
                return false;
            }

            return true;
        }));

        if (ret) {
            log.info("delete sys user >>> deleteToken. userId:{}", userId);
            // 删除redis中 token
            commonSessionService.deleteToken(SystemType.ADMIN, userId);
        }

        return ret;
    }

    @Override
    public List<Long> getRoleIdsByUserId(Long userId) {
        if (Objects.equals(SecurityConstants.SUPER_ADMIN_USER_ID, userId)) {
            return List.of(SecurityConstants.SUPER_ADMIN_ROLE_ID);
        }
        return sysUserRoleService.lambdaQuery().eq(SysUserRole::getSysUserId, userId).list().stream()
                .map(SysUserRole::getRoleId).toList();
    }

    @Override
    public List<String> getPermsByUserId(Long userId) {
        List<String> permsList;
        if (Objects.equals(SecurityConstants.SUPER_ADMIN_USER_ID, userId)) {
            permsList = sysMenuService.lambdaQuery().eq(SysMenu::getType, MenuTypeEnum.BUTTON.getCode()).list().stream()
                    .map(SysMenu::getPerms).filter(StringUtils::isNotBlank).distinct().toList();
        } else {
            permsList = sysMenuService.queryPermsByUserId(userId).stream().filter(StringUtils::isNotBlank).distinct().toList();
        }

        return permsList.stream().flatMap(perms -> {
                    if (StringUtils.isBlank(perms)) {
                        return null;
                    }
                    return Arrays.stream(perms.trim().split(CommonConstant.COMMA));
                }
        ).filter(StringUtils::isNotBlank).distinct().toList();
    }
}
