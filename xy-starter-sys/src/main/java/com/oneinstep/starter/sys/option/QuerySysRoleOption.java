package com.oneinstep.starter.sys.option;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.oneinstep.starter.core.dao.MybatisPlusQuery;
import com.oneinstep.starter.core.dao.PageOption;
import com.oneinstep.starter.sys.bean.domain.SysRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 角色查询参数
 **/
@ToString
@Getter
@Setter
@Schema(description = "角色查询参数")
public class QuerySysRoleOption extends PageOption implements MybatisPlusQuery<SysRole> {
    /**
     * 角色id
     */
    @Schema(description = "角色id")
    private Long id;
    /**
     * 角色名称
     */
    @Schema(description = "角色名称")
    private String roleName;
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
    /**
     * 创建者ID
     */
    @Schema(description = "创建者ID")
    private Long creatorId;
    /**
     * 状态 0-禁用 1-正常
     */
    @Schema(description = "状态 0-禁用 1-正常")
    private Integer status;


    @Override
    public QueryWrapper<SysRole> toQuery() {
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id)
                .like(roleName != null, "role_name", roleName)
                .like(remark != null, "remark", remark)
                .eq(creatorId != null, "creator_id", creatorId)
                .eq(status != null, "status", status);
        return queryWrapper;
    }

}
