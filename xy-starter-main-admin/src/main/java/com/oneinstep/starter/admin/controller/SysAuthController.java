package com.oneinstep.starter.admin.controller;

import com.oneinstep.starter.core.log.annotition.Logging;
import com.oneinstep.starter.core.response.Result;
import com.oneinstep.starter.core.utils.IPUtil;
import com.oneinstep.starter.core.validation.ChangeOtherPasswordGroup;
import com.oneinstep.starter.core.validation.ChangeOwnPasswordGroup;
import com.oneinstep.starter.security.admin.service.SysAuthService;
import com.oneinstep.starter.security.bean.bo.TokenUserInfoBO;
import com.oneinstep.starter.security.bean.dto.req.ChangePasswordReqDTO;
import com.oneinstep.starter.security.bean.dto.req.LoginReqDTO;
import com.oneinstep.starter.security.bean.dto.res.LoginResultDTO;
import com.oneinstep.starter.security.constant.LoginMethodEnum;
import com.oneinstep.starter.security.context.AuthedUserInfoContext;
import com.oneinstep.starter.security.service.AccountOwnerService;
import com.oneinstep.starter.security.utils.AuthorHeaderUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.oneinstep.starter.core.error.SecurityCodeAndMsgError.NOT_LOGIN;

/**
 * 后台登录、登出
 **/
@Slf4j
@RestController
@RequestMapping("/admin/auth")
@Tag(name = "后台登录、登出，密码修改")
@Validated
public class SysAuthController {

    @Resource
    private SysAuthService authService;
    @Resource
    private AccountOwnerService accountOwnerService;

    /**
     * 后台系统登录
     *
     * @param loginReqDTO 登录参数
     * @return token
     */
    @PostMapping("adminLogin")
    @Operation(summary = "后台系统登录", description = "后台系统登录")
    @Logging(printArgs = true, printResult = true, printError = true)
    public Result<LoginResultDTO> adminLogin(@Valid @RequestBody LoginReqDTO loginReqDTO, HttpServletRequest request) {
        loginReqDTO.setLoginIp(IPUtil.getRemoteIpAddress(request));
        loginReqDTO.setLoginMethod(LoginMethodEnum.USERNAME_PASSWORD);
        return Result.ok(authService.login(loginReqDTO));
    }

    /**
     * 登出后台系统
     *
     * @param request 请求
     * @return 是否登出成功
     */
    @PostMapping("adminLogout")
    @Operation(summary = "后台系统登出", description = "后台系统登出")
    @Logging(printArgs = true, printResult = true, printError = true)
    public Result<Void> adminLogout(HttpServletRequest request) {
        String token = AuthorHeaderUtil.getTokenFromRequest(request);
        authService.logout(token);
        return Result.ok();
    }

    /**
     * 修改 自己密码
     *
     * @param option 修改 自己密码 参数
     * @return 是否修改成功
     */
    @PostMapping("changeOwnPassword")
    @Operation(summary = "修改 自己密码", description = "修改 自己密码")
    @Logging(printArgs = true, printResult = true, printError = true)
    public Result<Void> changeOwnPassword(@Validated(ChangeOwnPasswordGroup.class) @RequestBody ChangePasswordReqDTO option) {

        TokenUserInfoBO accountOwnerBO = AuthedUserInfoContext.get();
        if (accountOwnerBO == null) {
            log.error("save adminUser error, not login");
            return Result.error(NOT_LOGIN);
        }

        Long userId = accountOwnerBO.getUserId();
        option.setUserId(userId);
        boolean success = authService.changeOwnPassword(option);
        if (!success) {
            log.error("changeOwnPassword error, changeOwnPassword failed");
            return Result.error();
        }

        return Result.ok();
    }

    /**
     * 修改 自己密码
     *
     * @return 是否修改成功
     */
    @PostMapping("resetOwnGoogleSecret")
    @Operation(summary = "重置自己的谷歌验证码", description = "重置自己的谷歌验证码")
    @Logging(printArgs = true, printResult = true, printError = true)
    public Result<Void> resetOwnGoogleSecret() {

        TokenUserInfoBO accountOwnerBO = AuthedUserInfoContext.get();
        if (accountOwnerBO == null) {
            log.error("resetOwnGoogleSecret error, not login.");
            return Result.error(NOT_LOGIN);
        }

        Long userId = accountOwnerBO.getUserId();
        boolean success = accountOwnerService.updateGoogleSecretKey(userId, "");
        if (!success) {
            log.info("resetOwnGoogleSecret error, updateSecretKey failed. userId:{}", userId);
            return Result.error();
        }

        return Result.ok();
    }

    /**
     * 修改 他人密码
     *
     * @param option 修改 他人密码 参数
     * @return 是否修改成功
     */
    @PostMapping("changeOtherPassword")
    @Operation(summary = "修改 他人密码", description = "修改 他人密码")
    @Logging(printArgs = true, printResult = true, printError = true)
    public Result<Void> changeOtherPassword(@Validated(ChangeOtherPasswordGroup.class) @RequestBody ChangePasswordReqDTO option) {

        TokenUserInfoBO accountOwnerBO = AuthedUserInfoContext.get();
        if (accountOwnerBO == null) {
            log.error("save adminUser error, not login");
            return Result.error(NOT_LOGIN);
        }

        boolean success = authService.changeOtherAccountPassword(option);
        if (!success) {
            log.error("changeOtherPassword error, changeOtherAccountPassword failed");
            return Result.error();
        }

        return Result.ok();
    }


    /**
     * 重置他人谷歌验证码
     *
     * @param userId 重置他人谷歌验证码 参数
     * @return 是否修改成功
     */
    @PostMapping("resetOtherGoogleSecret")
    @Operation(summary = "重置他人谷歌验证码", description = "重置他人谷歌验证码")
    public Result<Void> resetOtherGoogleSecret(@RequestParam Long userId) {

        TokenUserInfoBO accountOwnerBO = AuthedUserInfoContext.get();
        if (accountOwnerBO == null) {
            log.error("resetOwnGoogleSecret error, not login");
            return Result.error(NOT_LOGIN);
        }

        boolean success = accountOwnerService.updateGoogleSecretKey(userId, "");
        if (!success) {
            log.info("resetOwnGoogleSecret error, updateSecretKey failed");
            return Result.error();
        }

        return Result.ok();
    }
}
