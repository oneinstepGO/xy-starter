package com.oneinstep.starter.security.api.service;

import cn.hutool.core.lang.Validator;
import com.oneinstep.starter.core.constants.CommonStatus;
import com.oneinstep.starter.core.error.SecurityCodeAndMsgError;
import com.oneinstep.starter.core.log.annotition.Logging;
import com.oneinstep.starter.security.bean.bo.TokenUserInfoBO;
import com.oneinstep.starter.security.bean.dto.req.ChangePasswordReqDTO;
import com.oneinstep.starter.security.bean.dto.req.LoginReqDTO;
import com.oneinstep.starter.security.bean.dto.res.LoginResultDTO;
import com.oneinstep.starter.security.constant.LoginMethodEnum;
import com.oneinstep.starter.security.constant.SystemType;
import com.oneinstep.starter.security.context.AuthedUserInfoContext;
import com.oneinstep.starter.security.exception.AccountNotExistException;
import com.oneinstep.starter.security.jwt.JwtTokenProvider;
import com.oneinstep.starter.security.service.*;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class ApiAuthService {

    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private JwtTokenProvider jwtTokenProvider;
    @Resource
    private JwtTokenStore jwtTokenStore;
    @Resource
    private TokenUserInfoStoreService tokenUserInfoStoreService;
    @Resource
    private AccountOwnerService accountOwnerService;
    @Resource
    private WrongPasswordCounter wrongPasswordCounter;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private GoogleAuthenticatorService googleAuthenticatorService;
    @Resource
    private CommonSessionService commonSessionService;

    @Logging(printArgs = true, printResult = true)
    public LoginResultDTO login(@NotNull LoginReqDTO loginReqDTO) {

        LoginResultDTO loginResultDTO = new LoginResultDTO();

        LoginMethodEnum loginMethod = loginReqDTO.getLoginMethod();
        if (loginMethod == null) {
            log.error("user login >>> loginMethod is null");
            throw SecurityCodeAndMsgError.LOGIN_METHOD_IS_NULL.toOneBaseException();
        }

        String accountId;

        if (loginMethod == LoginMethodEnum.EMAIL_PASSWORD) {
            if (StringUtils.isBlank(loginReqDTO.getEmail())) {
                log.error("user login >>> email is blank");
                throw SecurityCodeAndMsgError.EMAIL_IS_BLANK.toOneBaseException();
            }
            boolean isEmail = Validator.isEmail(loginReqDTO.getEmail());
            if (!isEmail) {
                log.error("user login >>>  email format is wrong");
                throw SecurityCodeAndMsgError.NOT_VALID_EMAIL.toOneBaseException();
            }
            accountId = loginReqDTO.getEmail();
        } else if (loginMethod == LoginMethodEnum.MOBILE_PASSWORD) {
            if (StringUtils.isBlank(loginReqDTO.getMobile())) {
                log.error("user login >>> mobile is blank");
                throw SecurityCodeAndMsgError.MOBILE_IS_BLANK.toOneBaseException();
            }
            boolean isMobile = Validator.isMobile(loginReqDTO.getMobile());
            if (!isMobile) {
                log.error("user login >>>  email format is wrong");
                throw SecurityCodeAndMsgError.NOT_VALID_MOBILE.toOneBaseException();
            }
            accountId = loginReqDTO.getMobile();
        } else {
            throw new UnsupportedOperationException("loginMethod not support: " + loginMethod);
        }

        if (StringUtils.isBlank(accountId)) {
            log.error("user login >>> accountId is blank");
            throw SecurityCodeAndMsgError.ACCOUNT_IS_BLANK.toOneBaseException();
        }

        // check wrong password times
        boolean overTimes = wrongPasswordCounter.isOverTimes(SystemType.ORDINARY, accountId);
        if (overTimes) {
            log.error("user login >>> accountId：{}, wrong password times over limit", accountId);
            throw SecurityCodeAndMsgError.WRONG_PASSWORD_TIMES_OVER_LIMIT.toOneBaseException();
        }

        // check username and password
        Authentication authentication;
        try {
            String authAccountId = String.format("%d:%s", loginReqDTO.getLoginMethod().getCode(), accountId);
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authAccountId, loginReqDTO.getPassword()));
        } catch (BadCredentialsException be) {
            log.error("user login >> accountId：{}, BadCredentialsException: {}", accountId, be.getMessage());
            wrongPasswordCounter.increaseCount(SystemType.ADMIN, accountId);
            throw SecurityCodeAndMsgError.WRONG_PASSWORD.toOneBaseException();
        }

        // 获取用户信息
        TokenUserInfoBO userSession = (TokenUserInfoBO) authentication.getPrincipal();

        Long userId = userSession.getUserId();

        // 校验谷歌验证码
        String googleSecret = userSession.getGoogleSecret();
        if (StringUtils.isNotBlank(googleSecret)) {
            log.info("user login >>> user has bind google auth, userId: {}", userId);
            if (StringUtils.isBlank(loginReqDTO.getGoogleCode())) {
                log.error("user login >>> user has bind google auth, but google code is blank, userId: {}", userId);
                throw SecurityCodeAndMsgError.WRONG_GOOGLE_CODE.toOneBaseException();
            }

            // 验证 google code
            int code = Integer.parseInt(loginReqDTO.getGoogleCode());
            boolean valid = googleAuthenticatorService.validateCode(userSession.getGoogleSecret(), code);
            if (!valid) {
                log.error("user login >>> wrong google code, userId: {}", userId);
                throw SecurityCodeAndMsgError.WRONG_GOOGLE_CODE.toOneBaseException();
            }
        }

        // 检查用户是否被禁用
        if (!Objects.equals(CommonStatus.NORMAL.getCode(), userSession.getStatus())) {
            log.error("user login >>> user is disabled, userId: {}", userId);
            throw SecurityCodeAndMsgError.ACCOUNT_IS_DISABLE.toOneBaseException();
        }

        // 生成 JWT
        String jwt = jwtTokenProvider.generateToken(userSession);
        // 保存 JWT 到 redis
        jwtTokenStore.storeToken(SystemType.ORDINARY, userId, jwt);

        // 保存账户信息到 redis
        tokenUserInfoStoreService.storeUserInfo(SystemType.ORDINARY, userId, userSession);

        // 更新最后登录时间和ip
        accountOwnerService.updateLastLoginTimeAndIp(userId, loginReqDTO.getLoginIp());

        // 设置认证信息
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 重置错误次数
        wrongPasswordCounter.resetCount(SystemType.ORDINARY, accountId);

        loginResultDTO.setTokenUserInfo(userSession);
        loginResultDTO.setJwt(jwt);

        return loginResultDTO;
    }

    @Logging(printArgs = true)
    public void logout(String token) {
        if (StringUtils.isNotBlank(token) && jwtTokenProvider.validateToken(token)) {
            Long userId = jwtTokenProvider.getUserId(token);
            String jwtFromRedis = jwtTokenStore.getJwtToken(SystemType.ADMIN, userId);
            if (StringUtils.isNotBlank(jwtFromRedis) && StringUtils.equals(token, jwtFromRedis)) {
                log.info("token is expired");
                commonSessionService.deleteToken(SystemType.ADMIN, userId);
            }
        }
        log.info("logout >>> no toke or token is expired");
    }

    /**
     * 修改自己的密码
     *
     * @param option 修改密码参数
     * @return 是否修改成功
     */
    public boolean changeOwnPassword(ChangePasswordReqDTO option) {
        TokenUserInfoBO accountOwnerBO = AuthedUserInfoContext.get();
        if (accountOwnerBO == null) {
            log.error("changeOwnPassword error, not login");
            return false;
        }
        Long userId = accountOwnerBO.getUserId();
        TokenUserInfoBO accountOwner = checkAndGetAccount(userId);

        // compare old password and the password in db
        checkOldAndNewIsSame(option, accountOwner, userId);

        // the new password can't be the same as the old password
        checkOldPassword(option, accountOwner, userId);

        boolean b = accountOwnerService.updateLoginPassword(userId, passwordEncoder.encode(option.getNewPassword()));
        if (b) {
            log.info("changeOwnPassword >>> success, userId: {}", userId);
            commonSessionService.deleteToken(SystemType.ORDINARY, userId);
        } else {
            log.error("changeOwnPassword >>> failed, userId: {}", userId);
        }

        return b;
    }

    private void checkOldAndNewIsSame(ChangePasswordReqDTO option, TokenUserInfoBO oldAccount, Long userId) {
        if (!passwordEncoder.matches(option.getOldPassword(), oldAccount.getPassword())) {
            log.error("changeOwnPassword >>> old password is wrong, userId: {}", userId);
            throw SecurityCodeAndMsgError.OLD_PASSWORD_IS_WRONG_WHEN_CHANGE_PASSWORD.toOneBaseException();
        }
    }

    private TokenUserInfoBO checkAndGetAccount(Long userId) {
        TokenUserInfoBO tokenUserInfoBO = accountOwnerService.queryUserByUserId(userId);
        if (tokenUserInfoBO == null) {
            log.error("changeOwnPassword >>> user not exist, userId: {}", userId);
            throw new AccountNotExistException();
        }
        return tokenUserInfoBO;
    }

    private void checkOldPassword(ChangePasswordReqDTO option, TokenUserInfoBO accountOwner, Long userId) {
        if (passwordEncoder.matches(option.getNewPassword(), accountOwner.getPassword())) {
            log.error("changeOwnPassword >>> new password can't be the same as the old password, userId: {}", userId);
            throw SecurityCodeAndMsgError.NEW_PASSWORD_CAN_NOT_BE_THE_SAME_AS_THE_OLD_PASSWORD.toOneBaseException();
        }
    }
}