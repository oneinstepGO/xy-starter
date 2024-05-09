package com.oneinstep.starter.security.bean.dto.res;

import lombok.*;

@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleAuthDTO {
    /**
     * 谷歌密钥
     */
    private String secretKey;
    /**
     * 谷歌二维码
     */
    private String qrCode;
}
