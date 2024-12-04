package com.oneinstep.starter.core.utils;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * AES 加密工具类
 **/
@Slf4j
@UtilityClass
public class AesUtil {

    private static final String KEY = "1234567890123456";
    private static final AES _AES;

    static {
        _AES = SecureUtil.aes(KEY.getBytes(StandardCharsets.UTF_8));
    }


    public static String encrypt(String content) {
        // 加密
        String encryptBase64
                = _AES.encryptBase64(content);
        log.info("encryptBase64=> {}", encryptBase64);

        return encryptBase64;
    }

    public static String decrypt(String content) {
        // 解密
        String decryptStr = null;
        try {
            decryptStr = _AES.decryptStr(content);
            log.info("decryptStr=> {}", decryptStr);
        } catch (Exception e) {
            log.error("decrypt error, content:{}", content, e);
        }

        return decryptStr;
    }

}
