package com.oneinstep.starter.sys.interceptor;

import com.oneinstep.starter.core.error.SecurityCodeAndMsgError;
import com.oneinstep.starter.core.exception.OneBaseException;
import com.oneinstep.starter.security.bean.bo.TokenUserInfoBO;
import com.oneinstep.starter.security.bean.dto.req.LoginReqDTO;
import com.oneinstep.starter.security.interceptor.LoginInterceptor;
import com.oneinstep.starter.sys.service.SysConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 系统登录拦截器
 */
@Component
@Slf4j
public class SysLoginInterceptor implements LoginInterceptor {

    @Resource
    private SysConfigService sysConfigService;

    @Override
    public void canAccess(LoginReqDTO loginReqDTO, TokenUserInfoBO tokenUserInfoBO) throws OneBaseException {
        // check login ip white list
        if (!checkLoginIpWhiteList(loginReqDTO, tokenUserInfoBO)) {
            log.error("login >>> login ip is not in white list, username: {}", tokenUserInfoBO.getAccountId());
            throw SecurityCodeAndMsgError.LOGIN_IP_IS_NOT_IN_WHITE_LIST.toOneBaseException();
        }
    }

    private boolean checkLoginIpWhiteList(LoginReqDTO loginReqDTO, TokenUserInfoBO tokenUserInfoBO) {
        String loginIp = loginReqDTO.getLoginIp();
        if (StringUtils.isBlank(loginIp)) {
            return false;
        }
        String loginWhiteList = tokenUserInfoBO.getLoginWhiteList();
        if (StringUtils.isBlank(loginWhiteList)) {
            log.info("login ip white list is blank, username: {}", tokenUserInfoBO.getAccountId());
            return true;
        }

        String globalWhiteList = sysConfigService.getGlobalWhiteList();
        if (StringUtils.isNotBlank(globalWhiteList)) {
            loginWhiteList = StringUtils.joinWith(",", loginWhiteList, globalWhiteList);
        }

        log.info("final login ip white list: {}", loginWhiteList);

        if (StringUtils.isBlank(loginWhiteList)) {
            log.info("final login ip white list is blank, username: {}", tokenUserInfoBO.getAccountId());
            return true;
        }
        String[] whiteList = loginWhiteList.split(",");
        for (String ip : whiteList) {
            if (loginIp.equals(ip)) {
                return true;
            }
        }

        return false;
    }

}
