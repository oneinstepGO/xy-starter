package com.oneinstep.starter.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oneinstep.starter.core.utils.BeanCopyUtils;
import com.oneinstep.starter.security.constant.SecurityConstants;
import com.oneinstep.starter.sys.bean.domain.SysMenu;
import com.oneinstep.starter.sys.bean.dto.SysMenuDTO;
import com.oneinstep.starter.sys.mapper.SysMenuMapper;
import com.oneinstep.starter.sys.service.SysMenuService;
import com.oneinstep.starter.sys.utils.MenuUtil;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : hahaha
 * @created : 9/6/2023
 * @since: 1.0.0
 **/
@Slf4j
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Resource
    private SysMenuMapper sysMenuMapper;

    @Override
    public List<SysMenuDTO> getMenuListByUserId(@NotNull Long userId) {
        // 用户的所有菜单信息
        List<SysMenu> sysMenus;
        //系统管理员，拥有最高权限
        if (userId == SecurityConstants.SUPER_ADMIN_USER_ID) {
            sysMenus = this.lambdaQuery().ne(SysMenu::getType, 2)
                    .orderByDesc(SysMenu::getId)
                    .orderByDesc(SysMenu::getOrderNum)
                    .list();
        } else {
            sysMenus = sysMenuMapper.listMenuByUserId(userId);
        }

        List<SysMenuDTO> sysMenuDTOList = sysMenus.stream().map(sysMenu -> BeanCopyUtils.copy(sysMenu, SysMenuDTO.class)).toList();

        // 构建菜单树
        return MenuUtil.buildMenusTree(sysMenuDTOList, false);
    }

    @Override
    public List<SysMenuDTO> listAllMenuAndPerm() {
        // 所有的菜单及权限
        List<SysMenu> sysMenus = this.lambdaQuery()
                .orderByDesc(SysMenu::getId)
                .orderByDesc(SysMenu::getOrderNum)
                .list();

        List<SysMenuDTO> sysMenuDTOList = sysMenus.stream().map(sysMenu -> BeanCopyUtils.copy(sysMenu, SysMenuDTO.class)).toList();

        // 构建菜单树
        return MenuUtil.buildMenusTree(sysMenuDTOList, true);

    }

    @Override
    public List<String> queryPermsByUserId(Long userId) {
        return sysMenuMapper.queryPermsByUserId(userId);
    }
}
