package com.oneinstep.starter.sys.enums;

import lombok.Getter;

/**
 * 菜单权限类型
 **/
@Getter
public enum MenuTypeEnum {

    /**
     * 目录
     */
    CATALOG(0, "目录"),
    /**
     * 菜单
     */
    MENU(1, "菜单"),
    /**
     * 按钮
     */
    BUTTON(2, "按钮");

    private final Integer code;

    private final String desc;

    MenuTypeEnum(final Integer code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

}
