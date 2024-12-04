package com.oneinstep.starter.security.bean.dto.res;

import com.oneinstep.starter.security.bean.bo.TokenUserInfoBO;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;


@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResultDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * jwt 令牌
     */
    private String jwt;

    /**
     * 账户信息
     */
    private TokenUserInfoBO tokenUserInfo;

}
