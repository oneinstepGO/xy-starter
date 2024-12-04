package com.oneinstep.starter.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oneinstep.starter.core.dao.PageConverter;
import com.oneinstep.starter.core.dao.PageData;
import com.oneinstep.starter.core.log.annotition.Logging;
import com.oneinstep.starter.core.response.Result;
import com.oneinstep.starter.core.utils.BeanCopyUtils;
import com.oneinstep.starter.core.validation.AdminUserGroup;
import com.oneinstep.starter.core.validation.DeleteGroup;
import com.oneinstep.starter.core.validation.SaveGroup;
import com.oneinstep.starter.core.validation.UpdateGroup;
import com.oneinstep.starter.security.bean.bo.TokenUserInfoBO;
import com.oneinstep.starter.security.context.AuthedUserInfoContext;
import com.oneinstep.starter.sys.bean.bo.SysUserBO;
import com.oneinstep.starter.sys.bean.domain.SysUser;
import com.oneinstep.starter.sys.bean.dto.AdminUserDTO;
import com.oneinstep.starter.sys.enums.AccountTypeEnum;
import com.oneinstep.starter.sys.option.QueryAdminUserOption;
import com.oneinstep.starter.sys.option.SaveOrUpdateAdminUserOption;
import com.oneinstep.starter.sys.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

import static com.oneinstep.starter.core.error.SecurityCodeAndMsgError.NOT_LOGIN;

/**
 * 管理员管理
 **/
@RestController
@RequestMapping("/admin/sys/sysUser")
@Tag(name = "管理员 管理")
@Slf4j
@Validated
public class SysUserController {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 分页查询管理员列表
     *
     * @param option 查询参数
     * @return 管理员列表
     */
    @GetMapping("list")
    @Operation(summary = "管理员列表", description = "管理员 分页列表")
    @Logging(printArgs = true, printError = true)
    @PreAuthorize("hasAuthority('admin:sys:sysUser:list')")
    public Result<PageData<AdminUserDTO>> list(QueryAdminUserOption option) {

        TokenUserInfoBO accountOwnerBO = AuthedUserInfoContext.get();
        if (accountOwnerBO == null) {
            log.error("list adminUser error, not login");
            return Result.error(NOT_LOGIN);
        }

        PageData<?> pageData = option.toPageData();
        Page<SysUser> page = PageConverter.convert(pageData);

        QueryWrapper<SysUser> queryWrapper = option.toQueryWrapper();

        Page<SysUser> doPage = sysUserService.page(page, queryWrapper);

        PageData<AdminUserDTO> dtoPage = PageConverter.copyWithoutData(doPage);

        if (CollectionUtils.isNotEmpty(doPage.getRecords())) {
            List<AdminUserDTO> dtoList = doPage.getRecords().stream().map(bo -> BeanCopyUtils.copy(bo, AdminUserDTO.class)).toList();
            // 如果不是超管，隐藏admin 账号
            if (AccountTypeEnum.isNotSuperAdmin(accountOwnerBO.getUserId())) {
                dtoList = dtoList.stream().filter(dto -> AccountTypeEnum.isNotSuperAdmin(dto.getUserId())).toList();
            }
            dtoPage.setDataList(dtoList);
        } else {
            dtoPage.setDataList(Collections.emptyList());
        }
        return Result.ok(dtoPage);
    }

    /**
     * 新增 管理员
     *
     * @param option 新增管理员参数
     * @return 新增管理员
     */
    @PostMapping("save")
    @Operation(summary = "新增 管理员", description = "新增 管理员")
    @Logging(printArgs = true, printResult = true, printError = true)
    @PreAuthorize("hasAuthority('admin:sys:sysUser:save') && hasAuthority('admin:sys:role:list')")
    public Result<Void> save(@RequestBody @Validated({SaveGroup.class, AdminUserGroup.class}) SaveOrUpdateAdminUserOption option) {

        SysUserBO adminUserBO = BeanCopyUtils.copy(option, SysUserBO.class);

        TokenUserInfoBO accountOwnerBO = AuthedUserInfoContext.get();
        if (accountOwnerBO == null) {
            log.error("save adminUser error, not login");
            return Result.error(NOT_LOGIN);
        }

        adminUserBO.setCreatorId(accountOwnerBO.getUserId());
        adminUserBO.setPassword(passwordEncoder.encode(option.getPassword()));

        return sysUserService.saveUser(adminUserBO) ? Result.ok() : Result.error();

    }

    /**
     * 修改 管理员
     *
     * @param option 修改管理员参数
     * @return 修改管理员
     */
    @PostMapping("update")
    @Logging(printArgs = true, printResult = true, printError = true)
    @Operation(summary = "修改 管理员", description = "修改 管理员")
    @PreAuthorize("hasAuthority('admin:sys:sysUser:update') && hasAuthority('admin:sys:role:list')")
    public Result<Void> update(@RequestBody @Validated({UpdateGroup.class, AdminUserGroup.class}) SaveOrUpdateAdminUserOption option) {
        return sysUserService.updateUser(BeanCopyUtils.copy(option, SysUserBO.class)) ? Result.ok() : Result.error();
    }

    /**
     * 删除 管理员
     *
     * @param option 管理员id
     * @return 删除 管理员
     */
    @PostMapping("delete")
    @Logging(printArgs = true, printResult = true, printError = true)
    @Operation(summary = "删除 管理员", description = "删除 管理员")
    @PreAuthorize("hasAuthority('admin:sys:sysUser:delete')")
    public Result<Void> delete(@RequestBody @NotNull @Validated({DeleteGroup.class}) SaveOrUpdateAdminUserOption option) {
        return sysUserService.deleteUser(option.getUserId()) ? Result.ok() : Result.error();
    }

}
