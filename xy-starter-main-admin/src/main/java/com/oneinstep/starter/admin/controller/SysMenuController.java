package com.oneinstep.starter.admin.controller;

import com.oneinstep.starter.core.log.annotition.Logging;
import com.oneinstep.starter.core.response.Result;
import com.oneinstep.starter.security.bean.bo.TokenUserInfoBO;
import com.oneinstep.starter.security.context.AuthedUserInfoContext;
import com.oneinstep.starter.security.service.AccountOwnerService;
import com.oneinstep.starter.sys.bean.dto.SysMenuDTO;
import com.oneinstep.starter.sys.service.SysMenuService;
import com.oneinstep.starter.sys.vo.SysMenuVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.oneinstep.starter.core.error.SecurityCodeAndMsgError.NOT_LOGIN;


/**
 * 菜单权限
 **/
@Slf4j
@RestController
@RequestMapping("/admin/sys/menu")
@Tag(name = "菜单权限 管理")
public class SysMenuController {

    @Resource
    private SysMenuService sysMenuService;
    @Resource
    private AccountOwnerService accountOwnerService;

    /**
     * 获取当前用户所拥有的菜单和权限
     *
     * @return 获取用户所拥有的菜单和权限
     */
    @Operation(summary = "获取权限", description = "获取当前用户所拥有的菜单和权限")
    @GetMapping("nav")
    @Logging(printArgs = true, printError = true)
    public Result<SysMenuVO> nav() {

        TokenUserInfoBO accountInfoBO = AuthedUserInfoContext.get();
        if (accountInfoBO == null) {
            return Result.error(NOT_LOGIN);
        }

        Long userId = accountInfoBO.getUserId();
        List<SysMenuDTO> sysMenuDTOList = sysMenuService.getMenuListByUserId(userId);

        SysMenuVO sysMenuVO = new SysMenuVO();

        sysMenuVO.setMenus(sysMenuDTOList);
        sysMenuVO.setPerms(accountOwnerService.queryPermsByUserId(userId));
        return Result.ok(sysMenuVO);

    }

    /**
     * 所有菜单及权限列表(用于新建、修改角色时 获取菜单的信息)
     *
     * @return 所有菜单及权限列表
     */
    @GetMapping("listAllMenuAndPerm")
    @Operation(summary = "权限列表", description = "获取所有菜单及权限列表(用于新建、修改角色时 获取菜单的信息)，无分页")
    @Logging(printArgs = true, printError = true)
    public Result<List<SysMenuDTO>> listAllMenuAndPerm() {
        return Result.ok(sysMenuService.listAllMenuAndPerm());
    }

}
