package com.oneinstep.starter.core.utils;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * RSA 加密工具类
 **/
@Slf4j
@UtilityClass
public class RSAUtil {

    private static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyqSVJvTrorfV2zUnk22hTls+VfDLVtllUhQYhZa7CTLc/mHlS65ZT3bKqsxepRPf6jThcVSjXTl3bb2fC4Q+KiWVNeiUFftkxel9mT/t/lXf2hFU9ZzIGAp3w4NGzgAh/D2Zlg66OSnwra7CPkQPpAduAF8HHs95GyvYNyy3U9c4+QN66prJjP7lTRx6LZf6QlH+vLSjNUVkWJkgNKAsTEaHY73u5oYv761a4QOyOQfc2ABciNDBz41/n0PZKrB8Zih69w3OeKQ/2cRVoQhSb6JO7nR5Lh3LLfAT7EqFDdzf5vXmI4f69WXNPoPmPNQ8Lr/j8hFTF/jJuXsAV4vT5QIDAQAB";
    private static final String PRIVATE_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDKpJUm9Ouit9XbNSeTbaFOWz5V8MtW2WVSFBiFlrsJMtz+YeVLrllPdsqqzF6lE9/qNOFxVKNdOXdtvZ8LhD4qJZU16JQV+2TF6X2ZP+3+Vd/aEVT1nMgYCnfDg0bOACH8PZmWDro5KfCtrsI+RA+kB24AXwcez3kbK9g3LLdT1zj5A3rqmsmM/uVNHHotl/pCUf68tKM1RWRYmSA0oCxMRodjve7mhi/vrVrhA7I5B9zYAFyI0MHPjX+fQ9kqsHxmKHr3Dc54pD/ZxFWhCFJvok7udHkuHcst8BPsSoUN3N/m9eYjh/r1Zc0+g+Y81Dwuv+PyEVMX+Mm5ewBXi9PlAgMBAAECggEABjPfdN/OTp695wXLflG+vD1CvTKvpqrk1QxUa9JfSzrJVOf+MsDVbv892NiElRz0n6CavfNPRM07gJprQdj8ZiHcXVgPIFUfkodBFu3EBSLvxSb+qL3xyfqYC936ztdmMuhXySYOQS2tdkOBIpLGv8iSb2R5PVIwbMp8xcOSoPhJfclN9a3NDbq/qRcbob9r3PwTsAZRsK4hpkS3rB6lJwwFQY1fMdcj1a/moZqTYQacz4vSsIiI4VWFVbenPwUwAQ8kiWvE6I/+n0q20eB3F6nUo/IFSQlWizCcuRJd2M58X2HkSZ1h3w/s9C+z4qoaEWF4s+uUA/qMJMrgoNEtQQKBgQD7AkgRLnSFFjcbUtH/l4X6rjxr/x1PN8L9kQp+38XuMs5tyCT1ibhin34OWBo2GlXxm00sjv9NaxiyGBH3/FZ0Xooj9FMUVHHnifZiMbq9wBoFfNwYlgH5WkLvZq+aNe+a2rpM7EZLrfjp7doK6VW/BRq3rq0BtMfVZTFVvg5s/QKBgQDOrBojKi+eHHPlDZc2VFc3bEZ+Dbvc7G5zfX4MKvQdNqvfuWE82EtrMpx/pRbKhxY/MF4qD1tbJBYGiuWyYyE9FkaXQf0Lxie6qwIBJFr8CgXSydJp5ybZKUmcxw+yis25tOiwgVEQd29wMKzpD9vPq1zuUaYtM2P8U2RI4kGrCQKBgE9buU01UsGA9Is+9AXK8iD/VTsPvUR+bLeeU6MpPHrCtWEgFg9LK3CC3GfMkvzn9qQcZZng2/auFXC5C3VL4kmIWkRjyqBy0lBR3gPNlSzIv3FcuHlNc/JSBzeJT5uB0e8zmBMZ4F+s8TWEbZtliiaKhpfsMeaIMWXWSHLDU+xBAoGAZbhPJP+ecDOQ1yvZQORCCQVBrMkVraawticN4u9BNx+xeGvFcC0E8b33SNv4W7YhPPiRYCIu1KzuKkoyw5oGwgo0tD3GTgU5iKaiVe2O2n4UX6r15Snf9QJdOMzA7R9kIgLog/ch7vjZ4F4UvKERjyTImIOXRqgEjix9L2Is8AECgYBGVip//QTb9JP+GaoGX5mfEHk+Hhqb6N0323WvbicTYbR/wDkDj9Or3F/zKa6LfPbIhW6obypzXbVcBUG8BihX2B5utuXTla9wWprAUuux+YMVxxHM1fv3lSnT6RevRiTb+wx7f4ip9coyFPkM5gfM0k23CWRc/3QbIM8EQUqVEw==";

    private static final RSA _RSA;

    static {
        _RSA = SecureUtil.rsa(PRIVATE_KEY, PUBLIC_KEY);
    }

    public static String encrypt(String content) {
        // use hutool RSAUtil
        // 加密
        String encryptBase64
                = _RSA.encryptBase64(content, cn.hutool.crypto.asymmetric.KeyType.PublicKey);

        log.info("encryptBase64=> {}", encryptBase64);
        return encryptBase64;
    }

    public static String decrypt(String content) {
        // use hutool RSAUtil
        // 解密
        String decryptStr = _RSA.decryptStr(content, cn.hutool.crypto.asymmetric.KeyType.PrivateKey);
        log.info("decryptStr=> {}", decryptStr);
        return decryptStr;
    }

}
