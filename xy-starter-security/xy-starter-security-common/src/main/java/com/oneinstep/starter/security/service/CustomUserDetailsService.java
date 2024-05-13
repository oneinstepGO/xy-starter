package com.oneinstep.starter.security.service;

import com.oneinstep.starter.security.bean.bo.TokenUserInfoBO;
import com.oneinstep.starter.security.constant.LoginMethodEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    @Resource
    private AccountOwnerService accountOwnerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (StringUtils.isBlank(username)) {
            log.error("adminLogin >>> username is blank");
            throw new UsernameNotFoundException("username is blank");
        }

        int loginMethod = Integer.parseInt(username.split(":")[0]);
        LoginMethodEnum loginMethodEnum = LoginMethodEnum.getByCode(loginMethod);
        if (loginMethodEnum == null) {
            log.error("login >>> loginMethod is null");
            throw new UsernameNotFoundException("loginMethod is null");
        }
        String accountId = username.split(":")[1];
        Optional<TokenUserInfoBO> optional = accountOwnerService.queryUserByAccountId(loginMethodEnum, accountId);
        if (optional.isEmpty()) {
            log.error("login >>> user not exist, accountId: {}", accountId);
            throw new UsernameNotFoundException("User not exist");
        }

        return optional.get();

    }

}