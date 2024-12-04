package com.oneinstep.starter.core.env;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 环境上下文
 **/
@Component
@Slf4j
public class EnvironmentContext {

    @Resource
    private Environment environment;

    private static final String[] DEV_OR_TEST_ENV = {"dev", "test"};
    private static final String[] TEST_ENV = {"test"};
    private static final String[] PROD_ENV = {"prod"};
    private static final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";

    public String getActiveProfile() {
        return environment.getProperty(SPRING_PROFILES_ACTIVE);
    }

    public boolean isDevOrTestEnv() {
        String activeProfile = environment.getProperty(SPRING_PROFILES_ACTIVE);
        for (String env : DEV_OR_TEST_ENV) {
            if (env.equals(activeProfile)) {
                return true;
            }
        }
        return false;
    }

    public boolean isTestEnv() {
        String activeProfile = environment.getProperty(SPRING_PROFILES_ACTIVE);
        for (String env : TEST_ENV) {
            if (env.equals(activeProfile)) {
                return true;
            }
        }
        return false;
    }

    public boolean isProdEnv() {
        String activeProfile = environment.getProperty(SPRING_PROFILES_ACTIVE);
        for (String env : PROD_ENV) {
            if (env.equals(activeProfile)) {
                return true;
            }
        }
        return false;
    }

}
