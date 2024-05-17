package com.oneinstep.starter.business.api.service;

public interface MobileVerifyService {
    /**
     * 验证手机号
     *
     * @param mobile     手机号
     * @param verifyCode 验证码
     * @return 验证结果
     */
    boolean verify(String mobile, String verifyCode);
}
