package com.oneinstep.starter.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oneinstep.starter.sys.bean.domain.SysMenu;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单权限 Mapper
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {
    /**
     * 根据用户id 获取菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Select("""
                SELECT DISTINCT m.id AS id,
                                    m.parent_id,
                                    m.name,
                                    url,
                                    m.type,
                                    m.icon,
                                    m.order_num
                    FROM t_sys_user_role ur
                             LEFT JOIN t_sys_role_menu rm ON ur.role_id = rm.role_id
                             LEFT JOIN t_sys_menu m ON m.`id` = rm.menu_id
                             LEFT JOIN t_sys_role r ON r.`id` = ur.role_id and r.`id` = rm.role_id
                    WHERE ur.sys_user_id = #{userId}
                      and m.type != 2
                    order by id, order_num\
            """)
    List<SysMenu> listMenuByUserId(@Param("userId") @NotNull Long userId);

    /**
     * 根据账户id查询账户权限
     *
     * @param userId 用户ID
     * @return 账户权限列表
     */
    @Select("""
            select distinct m.perms
                    from t_sys_user_role ur
                             LEFT JOIN t_sys_role_menu rm on ur.role_id = rm.role_id
                             LEFT JOIN t_sys_menu m on rm.menu_id = m.id and m.type = 2
                    where ur.sys_user_id = #{userId} and m.perms is not null and m.perms != ''""")
    List<String> queryPermsByUserId(@NotNull Long userId);
}
