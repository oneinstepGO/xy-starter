package com.oneinstep.starter.sys.bean.dto;

import com.oneinstep.starter.sys.bean.domain.SysMenu;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


/**
 * 菜单管理
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysMenuDTO extends SysMenu {

    /**
     * 子菜单列表
     */
    private List<SysMenuDTO> childList;

}
