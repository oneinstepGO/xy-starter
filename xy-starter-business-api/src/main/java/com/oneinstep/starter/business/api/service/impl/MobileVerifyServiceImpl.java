package com.oneinstep.starter.business.api.service.impl;

import com.oneinstep.starter.business.api.service.MobileVerifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MobileVerifyServiceImpl implements MobileVerifyService {
    @Override
    public boolean verify(String mobile, String verifyCode) {
        log.info("verify mobile: {}, verifyCode: {}", mobile, verifyCode);
        return true;
    }
}
