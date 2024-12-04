package com.oneinstep.starter.sys.vo;

import com.oneinstep.starter.sys.bean.dto.SysMenuDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 菜单权限
 **/
@Getter
@Setter
@ToString
@Schema(description = "登陆成功返回的菜单和权限")
public class SysMenuVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 菜单
     */
    @Schema(description = "菜单列表，树结构")
    private List<SysMenuDTO> menus;
    /**
     * 权限
     */
    @Schema(description = "权限列表")
    private List<String> perms;

}
