package com.oneinstep.starter.api.controller;

import com.oneinstep.starter.core.log.annotition.Logging;
import com.oneinstep.starter.core.response.Result;
import com.oneinstep.starter.core.utils.IPUtil;
import com.oneinstep.starter.core.validation.ChangeOwnPasswordGroup;
import com.oneinstep.starter.security.api.service.ApiAuthService;
import com.oneinstep.starter.security.bean.bo.TokenUserInfoBO;
import com.oneinstep.starter.security.bean.dto.req.ChangePasswordReqDTO;
import com.oneinstep.starter.security.bean.dto.req.LoginReqDTO;
import com.oneinstep.starter.security.bean.dto.res.LoginResultDTO;
import com.oneinstep.starter.security.context.AuthedUserInfoContext;
import com.oneinstep.starter.security.service.AccountOwnerService;
import com.oneinstep.starter.security.utils.AuthorHeaderUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.oneinstep.starter.core.error.SecurityCodeAndMsgError.NOT_LOGIN;

/**
 * 后台登录、登出
 **/
@Slf4j
@RestController
@RequestMapping("/api/auth")
@Validated
public class ApiAuthController {

    @Resource
    private ApiAuthService apiAuthService;
    @Resource
    private AccountOwnerService accountOwnerService;

    /**
     * 登录
     *
     * @param loginReqDTO 登录参数
     * @return token
     */
    @PostMapping("login")
    @Logging(printArgs = true, printResult = true, printError = true)
    public Result<LoginResultDTO> login(@Valid @RequestBody LoginReqDTO loginReqDTO, HttpServletRequest request) {
        loginReqDTO.setLoginIp(IPUtil.getRemoteIpAddress(request));
        return Result.ok(apiAuthService.login(loginReqDTO));
    }

    /**
     * 登出
     *
     * @param request 请求
     * @return 是否登出成功
     */
    @PostMapping("logout")
    @Logging(printArgs = true, printResult = true, printError = true)
    public Result<Void> logout(HttpServletRequest request) {
        String token = AuthorHeaderUtil.getTokenFromRequest(request);
        apiAuthService.logout(token);
        return Result.ok();
    }

    /**
     * 修改 自己密码
     *
     * @param option 修改 自己密码 参数
     * @return 是否修改成功
     */
    @PostMapping("changeOwnPassword")
    @Logging(printArgs = true, printResult = true, printError = true)
    public Result<Void> changeOwnPassword(@Validated(ChangeOwnPasswordGroup.class) @RequestBody ChangePasswordReqDTO option) {

        TokenUserInfoBO tokenUserInfoBO = AuthedUserInfoContext.get();
        if (tokenUserInfoBO == null) {
            log.error("changeOwnPassword error, not login");
            return Result.error(NOT_LOGIN);
        }

        Long userId = tokenUserInfoBO.getUserId();
        option.setUserId(userId);
        boolean success = apiAuthService.changeOwnPassword(option);
        if (!success) {
            log.error("changeOwnPassword error, changeOwnPassword failed");
            return Result.error();
        }

        return Result.ok();
    }

    /**
     * 修改 自己的谷歌验证码
     *
     * @return 是否修改成功
     */
    @PostMapping("resetOwnGoogleSecret")
    @Logging(printArgs = true, printResult = true, printError = true)
    public Result<Void> resetOwnGoogleSecret() {

        TokenUserInfoBO tokenUserInfoBO = AuthedUserInfoContext.get();
        if (tokenUserInfoBO == null) {
            log.error("resetOwnGoogleSecret error, not login.");
            return Result.error(NOT_LOGIN);
        }

        Long userId = tokenUserInfoBO.getUserId();
        boolean success = accountOwnerService.updateGoogleSecretKey(userId, "");
        if (!success) {
            log.info("resetOwnGoogleSecret error, updateSecretKey failed. userId:{}", userId);
            return Result.error();
        }

        return Result.ok();
    }
}
