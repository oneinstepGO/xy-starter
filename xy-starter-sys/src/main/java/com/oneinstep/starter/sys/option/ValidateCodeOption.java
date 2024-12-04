package com.oneinstep.starter.sys.option;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 验证码参数
 **/
@ToString
@Getter
@Setter
public class ValidateCodeOption implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "验证码不能为空")
    private Integer code;
}
