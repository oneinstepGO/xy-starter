package com.oneinstep.starter.core.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Slf4j
@UtilityClass
public class MD5Util {
    /**
     * 计算MD5
     *
     * @param input 输入
     * @return MD5
     */
    public static String calculateMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("calculateMD5 error", e);
            return null;
        }
    }

    /**
     * 计算MD5
     * 输出小写字母
     */
    public static String calculateMD5UpperCase(String input) {
        return Objects.requireNonNull(calculateMD5(input)).toUpperCase();
    }

    /**
     * SHA256 处理
     */
    public static String calculateSHA256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("calculateSHA256 error", e);
            return null;
        }

    }

}
