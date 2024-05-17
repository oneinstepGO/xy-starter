package com.oneinstep.starter.business.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oneinstep.starter.business.api.bean.domain.User;
import com.oneinstep.starter.business.api.bean.dto.req.UserRegisterReqDTO;
import com.oneinstep.starter.business.api.mapper.UserMapper;
import com.oneinstep.starter.business.api.service.UserService;
import com.oneinstep.starter.core.constants.CommonStatus;
import com.oneinstep.starter.core.utils.SnowflakeGenerator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean register(UserRegisterReqDTO registerReqDTO) {
        // 检查手机号是否已注册
        if (count(new LambdaQueryWrapper<User>().eq(User::getMobile, registerReqDTO.getMobile())) > 0) {
            log.error("手机号已注册，无法重新注册");
            return false;
        }
        User user = new User();
        user.setNickName(registerReqDTO.getNickName());
        user.setMobile(registerReqDTO.getMobile());
        user.setLoginPassword(passwordEncoder.encode(registerReqDTO.getLoginPassword()));
        user.setGender(registerReqDTO.getGender());
        user.setBirthday(registerReqDTO.getBirthday());
        user.setAvatar(registerReqDTO.getAvatar());
        user.setEmail(registerReqDTO.getEmail());
        user.setWxOpenId(registerReqDTO.getWxOpenId());
        user.setUserId(SnowflakeGenerator.getInstance().generateId());
        user.setRegTime(LocalDateTime.now());
        user.setStatus(CommonStatus.NORMAL.getCode());
        user.setRegIp(registerReqDTO.getRegIp());


        return save(user);
    }
}
