package com.oneinstep.starter.sys.option;

import com.oneinstep.starter.core.validation.DeleteGroup;
import com.oneinstep.starter.core.validation.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 保存/修改管理员参数
 **/
@ToString
@Getter
@Setter
@Schema(description = "管理员保存/修改参数")
public class SaveOrUpdateAdminUserOption extends SaveOrUpdateSysUserOption {
    /**
     * 用户ID
     */
    @Schema(description = "用户id")
    @NotNull(groups = {UpdateGroup.class, DeleteGroup.class}, message = "用户id不能为空")
    @Min(value = 2, groups = {UpdateGroup.class, DeleteGroup.class}, message = "该用户不允许编辑")
    private Long userId;
}
