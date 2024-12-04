package com.oneinstep.starter.admin.controller;

import com.oneinstep.starter.core.log.annotition.Logging;
import com.oneinstep.starter.core.response.Result;
import com.oneinstep.starter.core.utils.BeanCopyUtils;
import com.oneinstep.starter.security.bean.bo.TokenUserInfoBO;
import com.oneinstep.starter.security.context.AuthedUserInfoContext;
import com.oneinstep.starter.security.service.AccountOwnerService;
import com.oneinstep.starter.sys.bean.dto.AccountOwnerDTO;
import com.oneinstep.starter.sys.bean.dto.AdminUserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.oneinstep.starter.core.error.SecurityCodeAndMsgError.NOT_LOGIN;

/**
 * 账户信息查询
 **/
@RestController
@RequestMapping("/admin/account/")
@Tag(name = "账户 管理")
@Slf4j
public class SysAccountController {

    @Resource
    private AccountOwnerService accountOwnerService;

    /**
     * 获取账户信息
     *
     * @return 账户信息
     */
    @GetMapping("info")
    @Operation(summary = "账户信息", description = "获取账户信息")
    @Logging(printArgs = true, printResult = true, printError = true)
    public Result<AccountOwnerDTO> info() {
        TokenUserInfoBO session = AuthedUserInfoContext.get();
        if (session == null) {
            log.error("info >>>>> not login");
            return Result.error(NOT_LOGIN);
        }

        Long userId = session.getUserId();
        TokenUserInfoBO tokenUserInfoBO = accountOwnerService.queryUserByUserId(userId);
        if (tokenUserInfoBO == null) {
            log.info("info >>>>> user not exist, userId: {}", userId);
            return Result.error();
        }

        AccountOwnerDTO dto = BeanCopyUtils.copy(tokenUserInfoBO, AdminUserDTO.class);
        dto.setAlreadyBindGoogleAuth(StringUtils.isNotBlank(tokenUserInfoBO.getGoogleSecret()));
        return Result.ok(dto);
    }

}
