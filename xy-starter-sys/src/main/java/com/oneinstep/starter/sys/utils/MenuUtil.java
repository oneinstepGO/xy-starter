package com.oneinstep.starter.sys.utils;

import com.oneinstep.starter.sys.bean.dto.SysMenuDTO;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单工具类
 **/
@UtilityClass
public class MenuUtil {

    /**
     * 构建菜单树
     *
     * @param sysMenuDTOList 菜单列表
     * @param needPermTree   是否需要权限树
     * @return 菜单树
     */
    public List<SysMenuDTO> buildMenusTree(List<SysMenuDTO> sysMenuDTOList, boolean needPermTree) {

        if (CollectionUtils.isEmpty(sysMenuDTOList)) {
            return Collections.emptyList();
        }

        Map<Long, List<SysMenuDTO>> sysMenuLevelMap = sysMenuDTOList.stream()
                .filter(m -> Objects.nonNull(m.getParentId()))
                .sorted(Comparator.comparing(SysMenuDTO::getOrderNum))
                .collect(Collectors.groupingBy(SysMenuDTO::getParentId));

        // 一级目录
        List<SysMenuDTO> rootMenuList = sysMenuLevelMap.get(0L);

        if (CollectionUtils.isEmpty(rootMenuList)) {
            return Collections.emptyList();
        }

        // 二级菜单
        for (SysMenuDTO rootMenu : rootMenuList) {
            List<SysMenuDTO> menuBOList = sysMenuLevelMap.get(rootMenu.getId());

            if (CollectionUtils.isNotEmpty(menuBOList) && needPermTree) {
                // 三级权限
                for (SysMenuDTO sysMenuDTO : menuBOList) {
                    sysMenuDTO.setChildList(sysMenuLevelMap.get(sysMenuDTO.getId()));
                }
            }

            rootMenu.setChildList(menuBOList);
        }

        return rootMenuList;
    }

}
