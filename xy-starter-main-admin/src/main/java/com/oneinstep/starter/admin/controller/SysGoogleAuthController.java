package com.oneinstep.starter.admin.controller;

import com.oneinstep.starter.core.response.Result;
import com.oneinstep.starter.security.bean.bo.TokenUserInfoBO;
import com.oneinstep.starter.security.bean.dto.res.GoogleAuthDTO;
import com.oneinstep.starter.security.context.AuthedUserInfoContext;
import com.oneinstep.starter.security.service.AccountOwnerService;
import com.oneinstep.starter.security.service.GoogleAuthenticatorService;
import com.oneinstep.starter.sys.option.ValidateCodeOption;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

import static com.oneinstep.starter.core.error.SecurityCodeAndMsgError.*;

/**
 * 谷歌验证码
 */
@RestController
@RequestMapping("/admin/googleAuth")
@Slf4j
@Validated
public class SysGoogleAuthController {
    @Resource
    private GoogleAuthenticatorService googleAuthenticatorService;
    @Resource
    private AccountOwnerService accountOwnerService;
    @Resource
    private RedissonClient redissonClient;

    private static final String ACCOUNT_INFO_PREFIX = "xy:account:googleAuth:%s";

    @GetMapping("/genGoogleAuth")
    public Result<GoogleAuthDTO> genGoogleAuth() {

        TokenUserInfoBO accountOwnerBO = AuthedUserInfoContext.get();
        if (accountOwnerBO == null) {
            log.info("bind >>>>> not login");
            return Result.error(NOT_LOGIN);
        }

        Long userId = accountOwnerBO.getUserId();
        GoogleAuthDTO googleAuthDTO = googleAuthenticatorService.genGoogleAuth(userId);
        String accountName = accountOwnerBO.getAccountId();

        if (googleAuthDTO == null) {
            log.info("bind >>>>> generate google auth failed");
            return Result.error();
        }

        // save secretKey to redis cache
        RBucket<String> bucket = redissonClient.getBucket(String.format(ACCOUNT_INFO_PREFIX, accountName));
        bucket.set(googleAuthDTO.getSecretKey());
        bucket.expire(Duration.ofMinutes(5));

        log.info("bind >>>>> secretKey saved to accountName:{}, redis:{}", accountName, googleAuthDTO.getSecretKey());

        return Result.ok(googleAuthDTO);
    }

    @PostMapping("/validateAndBindCode")
    public Result<Void> validateAndBindCode(@RequestBody @Validated ValidateCodeOption option) {
        TokenUserInfoBO accountOwnerBO = AuthedUserInfoContext.get();
        if (accountOwnerBO == null) {
            log.info("validateCode >>>>> not login");
            return Result.error(NOT_LOGIN);
        }

        Long userId = accountOwnerBO.getUserId();

        String username = accountOwnerBO.getAccountId();

        // get secretKey from redis cache
        RBucket<String> bucket = redissonClient.getBucket(String.format(ACCOUNT_INFO_PREFIX, username));
        String secretKey = bucket.get();
        log.info("validateCode >>>>> secretKey from redis:{}", secretKey);

        if (StringUtils.isBlank(secretKey)) {
            log.error("validateCode >>>>> there is no secretKey in redis, userId:{}", userId);
            return Result.error(EXPIRED_GOOGLE_CODE);
        }
        boolean valid = googleAuthenticatorService.validateCode(secretKey, option.getCode());
        if (!valid) {
            log.error("validateCode >>>>> wrong google code, userId:{}", userId);
            return Result.error(WRONG_GOOGLE_CODE);
        }
        // save secretKey to database
        log.info("谷歌验证码验证成功，更新 用户 {} 的 secretKey, secretKey:{}", userId, secretKey);
        boolean success = accountOwnerService.updateGoogleSecretKey(userId, secretKey);

        if (success) {
            // delete secretKey from redis cache
            bucket.delete();
        }
        return success ? Result.ok() : Result.error();
    }

}
