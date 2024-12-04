package com.oneinstep.starter.sys.option;

import com.oneinstep.starter.core.validation.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 保存/修改角色参数
 **/
@ToString
@Getter
@Setter
@Schema(description = "角色保存/修改参数")
public class SaveOrUpdateSysRoleOption implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色id
     */
    @Schema(description = "角色id")
    @NotNull(groups = {UpdateGroup.class})
    private Long id;
    /**
     * 角色名称
     */
    @NotEmpty(message = "角色名称不能为空")
    @Schema(description = "角色名称")
    private String roleName;
    /**
     * 角色备注
     */
    @Schema(description = "角色备注")
    private String remark;
    /**
     * 状态 0-禁用 1-启用
     */
    @Schema(description = "状态 0-禁用 1-启用")
    @NotNull(groups = {UpdateGroup.class})
    private Integer status;
    /**
     * 角色对应的菜单及权限id列表
     */
    @Schema(description = "角色对应的菜单及权限id列表")
    @NotEmpty
    @Min(value = 1, message = "权限ID不能为空")
    @Max(value = 200, message = "权限ID不能超过200个")
    private List<Long> menuIdList;

}
