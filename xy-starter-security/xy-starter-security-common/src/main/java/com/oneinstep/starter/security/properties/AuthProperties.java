package com.oneinstep.starter.security.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 认证配置
 **/
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "xy.starter.auth")
public class AuthProperties {

    private static final String[] DEFAULT_FILTER_URLS = {"/admin/auth/**", "/static/doc/**"};

    /**
     * 不验证的url
     */
    private String[] filterUrls = DEFAULT_FILTER_URLS;
    /**
     * 错误密码最多次数
     */
    private Long wrongPasswordTimes = 10L;
    /**
     * 错误密码锁定时间
     */
    private Long wrongPasswordLockTimeSeconds = 1800L;
    /**
     * jwt token secret
     */
    private String jwtSecret;
    /**
     * jwt token 过期时间
     */
    private Long jwtExpiration = 43200000L;

}
