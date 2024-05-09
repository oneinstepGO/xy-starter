package com.oneinstep.starter.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oneinstep.starter.common.error.CommonCodeAndMsgError;
import com.oneinstep.starter.core.constants.CommonStatus;
import com.oneinstep.starter.core.dao.PageConverter;
import com.oneinstep.starter.core.dao.PageData;
import com.oneinstep.starter.core.log.annotition.Logging;
import com.oneinstep.starter.core.response.Result;
import com.oneinstep.starter.core.utils.BeanCopyUtils;
import com.oneinstep.starter.security.bean.bo.TokenUserInfoBO;
import com.oneinstep.starter.security.context.AuthedUserInfoContext;
import com.oneinstep.starter.sys.bean.bo.SysRoleBO;
import com.oneinstep.starter.sys.bean.domain.SysRole;
import com.oneinstep.starter.sys.bean.dto.SysRoleDTO;
import com.oneinstep.starter.sys.option.QuerySysRoleOption;
import com.oneinstep.starter.sys.option.SaveOrUpdateSysRoleOption;
import com.oneinstep.starter.sys.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.oneinstep.starter.core.error.BaseCodeAndMsgError.ILLEGAL_ARGUMENT;

/**
 * 系统角色
 **/
@RestController
@RequestMapping("/admin/sys/role")
@Tag(name = "角色 管理")
public class SysRoleController {

    @Resource
    private SysRoleService sysRoleService;

    /**
     * 系统角色列表
     *
     * @return 角色列表
     */
    @GetMapping("list")
    @Operation(summary = "角色 列表", description = "角色 分页列表查询")
    @Logging(printArgs = true, printError = true)
    public Result<PageData<SysRoleDTO>> list(QuerySysRoleOption option) {
        PageData<?> pageData = option.toPageData();
        Page<SysRole> page = PageConverter.convert(pageData);
        Page<SysRole> pageResult = sysRoleService.page(page, option.toQuery());

        PageData<SysRoleDTO> dtoPage = PageConverter.copyWithoutData(pageResult);


        if (CollectionUtils.isEmpty(pageResult.getRecords())) {
            dtoPage.setDataList(Collections.emptyList());
        } else {
            List<SysRoleDTO> sysRoleDTOS = pageResult.getRecords().stream().map(sysRole -> BeanCopyUtils.copy(sysRole, SysRoleDTO.class)).toList();
            dtoPage.setDataList(sysRoleDTOS);
        }
        return Result.ok(dtoPage);
    }

    /**
     * 角色信息 包含菜单和权限
     *
     * @param roleId 角色ID
     * @return 角色信息 包含菜单和权限
     */
    @GetMapping("info")
    @Operation(summary = "获取角色信息", description = "获取角色信息")
    @Logging(printArgs = true, printResult = true, printError = true)
    public Result<SysRoleDTO> info(@NotNull @RequestParam("roleId") Long roleId) {
        SysRoleBO sysRoleBO = sysRoleService.queryRoleInfoIncludeMenusAndPerms(roleId);
        if (sysRoleBO == null) {
            return Result.error(CommonCodeAndMsgError.QUERY_RESOURCE_NOT_EXIST, "角色不存在");
        }

        return Result.ok(BeanCopyUtils.copy(sysRoleBO, SysRoleDTO.class));
    }

    /**
     * 保存角色
     *
     * @param option 角色信息
     * @return 是否保存成功
     */
    @PostMapping("save")
    @Operation(summary = "保存角色", description = "保存角色")
    @Logging(printArgs = true, printResult = true, printError = true)
    public Result<Void> save(@RequestBody SaveOrUpdateSysRoleOption option) {
        SysRoleBO sysRoleBO = BeanCopyUtils.copy(option, SysRoleBO.class);

        TokenUserInfoBO accountInfoBO = AuthedUserInfoContext.get();
        Long userId = accountInfoBO.getUserId();
        sysRoleBO.setCreatorId(userId);
        boolean success = sysRoleService.saveRoleAndRoleMenu(sysRoleBO);
        if (success) {
            return Result.ok();
        } else {
            return Result.error();
        }
    }

    @PostMapping("update")
    @Operation(summary = "修改角色", description = "修改角色")
    @Logging(printArgs = true, printResult = true, printError = true)
    public Result<Void> update(@RequestBody SaveOrUpdateSysRoleOption option) {
        if (option.getId() == null) {
            return Result.error(ILLEGAL_ARGUMENT, "角色id不能为空");
        }
        if (option.getStatus() == null || (!Objects.equals(CommonStatus.NORMAL.getCode(), option.getStatus()) && !Objects.equals(CommonStatus.DISABLE.getCode(), option.getStatus()))) {
            return Result.error(ILLEGAL_ARGUMENT, "角色状态不能为空, 或者角色状态值不合法");
        }
        SysRoleBO sysRoleBO = BeanCopyUtils.copy(option, SysRoleBO.class);
        boolean success = sysRoleService.updateRoleAndRoleMenu(sysRoleBO);
        if (success) {
            return Result.ok();
        } else {
            return Result.error();
        }
    }

    /**
     * 查询 所有角色id 和 名称列表，用于给用户分配角色
     *
     * @return 查询 所有角色id 和 名称列表，用于给用户分配角色
     */
    @Operation(summary = "所有角色", description = "查询 所有角色id 和 名称列表，用于给用户分配角色")
    @GetMapping("listAll")
    @Logging(printArgs = true, printResult = true, printError = true)
    public Result<List<SysRoleDTO>> listAll() {
        List<SysRoleBO> sysRoleBOS = sysRoleService.listAllRoleIdAndNames();
        if (CollectionUtils.isEmpty(sysRoleBOS)) {
            return Result.ok(Collections.emptyList());
        }
        List<SysRoleDTO> sysRoleDTOS = sysRoleBOS.stream().map(sysRoleBO -> BeanCopyUtils.copy(sysRoleBO, SysRoleDTO.class)).toList();

        return Result.ok(sysRoleDTOS);
    }

}
