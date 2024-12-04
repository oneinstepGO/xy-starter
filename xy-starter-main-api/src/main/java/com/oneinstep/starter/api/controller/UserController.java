package com.oneinstep.starter.api.controller;

import com.oneinstep.starter.business.api.bean.domain.User;
import com.oneinstep.starter.business.api.bean.dto.res.UserDTO;
import com.oneinstep.starter.business.api.service.UserService;
import com.oneinstep.starter.core.response.Result;
import com.oneinstep.starter.core.utils.BeanCopyUtils;
import com.oneinstep.starter.security.bean.bo.TokenUserInfoBO;
import com.oneinstep.starter.security.context.AuthedUserInfoContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.oneinstep.starter.common.error.CommonCodeAndMsgError.QUERY_RESOURCE_NOT_EXIST;
import static com.oneinstep.starter.core.error.SecurityCodeAndMsgError.NOT_LOGIN;

@Slf4j
@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("info")
    public Result<UserDTO> info() {
        TokenUserInfoBO tokenUserInfoBO = AuthedUserInfoContext.get();
        if (tokenUserInfoBO == null) {
            return Result.error(NOT_LOGIN);
        }
        log.info("tokenUserInfoBO: {}", tokenUserInfoBO);
        Long userId = tokenUserInfoBO.getUserId();

        User user = userService.getById(userId);
        if (user == null) {
            return Result.error(QUERY_RESOURCE_NOT_EXIST, "用户不存在");
        }
        UserDTO userDTO = BeanCopyUtils.copy(user, UserDTO.class);
        return Result.ok(userDTO);
    }
}
