package com.oneinstep.starter.security.bean.dto.req;

import com.oneinstep.starter.core.validation.ChangeOtherPasswordGroup;
import com.oneinstep.starter.core.validation.ChangeOwnPasswordGroup;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改密码参数
 **/
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordReqDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 要修改密码的用户名
     */
    @NotNull(groups = {ChangeOtherPasswordGroup.class}, message = "要修改密码的用户名不能为空")
    private Long userId;
    /**
     * 旧密码
     */
    @NotEmpty(groups = {ChangeOwnPasswordGroup.class}, message = "旧密码不能为空")
    private String oldPassword;

    /**
     * 新密码
     */
    @NotEmpty(message = "新密码不能为空")
    @Min(value = 6, message = "密码长度不能小于6位")
    @Max(value = 20, message = "密码长度不能大于20位")
    private String newPassword;

}
