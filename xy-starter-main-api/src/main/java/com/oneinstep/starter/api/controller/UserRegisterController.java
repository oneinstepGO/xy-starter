package com.oneinstep.starter.api.controller;

import com.oneinstep.starter.business.api.bean.dto.req.UserRegisterReqDTO;
import com.oneinstep.starter.business.api.service.MobileVerifyService;
import com.oneinstep.starter.business.api.service.UserService;
import com.oneinstep.starter.core.response.Result;
import com.oneinstep.starter.core.utils.IPUtil;
import com.oneinstep.starter.core.utils.MobileUtil;
import com.oneinstep.starter.core.utils.RandomUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserRegisterController {

    @Resource
    private UserService userService;
    @Resource
    private MobileVerifyService mobileVerifyService;

    /**
     * 注册接口
     * 注册方式：
     * 手机号+手机验证码+密码
     * 1.1 验证手机
     * 1.2 设置密码
     * 1.3 设置昵称、性别、生日、头像
     */
    @PostMapping("register")
    public Result<Void> register(@Valid @RequestBody UserRegisterReqDTO registerReqDTO, HttpServletRequest request) {
        log.info("registerReqDTO: {}", registerReqDTO);
        // 1.1 验证手机
        boolean verify = mobileVerifyService.verify(registerReqDTO.getMobile(), registerReqDTO.getVerifyCode());
        if (!verify) {
            return Result.error("手机验证码错误");
        }

        // 如果昵称为空 ，设置昵称为 6位随机字符串+手机
        if (StringUtils.isBlank(registerReqDTO.getNickName())) {
            registerReqDTO.setNickName(RandomUtil.randomString(6) + "_" + MobileUtil.getMobileLastFour(registerReqDTO.getMobile()));
        }

        String remoteIpAddress = IPUtil.getRemoteIpAddress(request);
        registerReqDTO.setRegIp(remoteIpAddress);
        boolean register = userService.register(registerReqDTO);
        if (!register) {
            return Result.error("注册失败");
        }

        return Result.ok();
    }

}
