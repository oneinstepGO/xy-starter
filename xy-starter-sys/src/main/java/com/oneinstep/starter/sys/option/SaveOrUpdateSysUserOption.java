package com.oneinstep.starter.sys.option;

import com.oneinstep.starter.core.validation.AdminUserGroup;
import com.oneinstep.starter.core.validation.SaveGroup;
import com.oneinstep.starter.core.validation.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 保存修改账户参数
 **/
@ToString
@Getter
@Setter
@Schema(description = "账户通用 保存/修改参数")
public class SaveOrUpdateSysUserOption implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    @NotNull(groups = {UpdateGroup.class}, message = "用户ID不能为空")
    private Long userId;
    /**
     * 用户名
     */
    @Schema(description = "用户名")
    @NotNull(groups = {SaveGroup.class}, message = "用户名不能为空")
    @Null(groups = {UpdateGroup.class}, message = "编辑用户时用户名不能修改")
    private String username;
    /**
     * 登录密码
     */
    @Schema(description = "登录密码")
    @NotNull(groups = {SaveGroup.class}, message = "密码不能为空")
    @Null(groups = {UpdateGroup.class}, message = "编辑用户时密码不能修改")
    private String password;
    /**
     * 昵称
     */
    @NotEmpty(message = "昵称不能为空")
    @Schema(description = "昵称")
    private String nickName;
    /**
     * 登录白名单，用英文逗号分隔，不设置表示允许所有
     */
    @Schema(description = "登录白名单，用英文逗号分隔，不设置表示允许所有")
    private String loginWhiteList;
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
    /**
     * 状态 0 禁用 1 正常
     */
    @Schema(description = "状态 0 禁用 1 正常")
    @NotNull(groups = {UpdateGroup.class}, message = "状态不能为空")
    private Integer status;
    /**
     * 角色ID列表
     */
    @NotEmpty(groups = {AdminUserGroup.class}, message = "角色ID列表不能为空")
    @Size(groups = {AdminUserGroup.class}, min = 1, max = 10, message = "角色ID列表长度为1-10")
    @Schema(description = "角色ID列表")
    private List<Long> roleIds;

}
