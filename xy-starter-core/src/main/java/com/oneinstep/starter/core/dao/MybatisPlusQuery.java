package com.oneinstep.starter.core.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

public interface MybatisPlusQuery<T> {

    /**
     * 转换为 Mybatis Plus 的 QueryWrapper
     *
     * @return QueryWrapper
     */
    QueryWrapper<T> toQuery();

}
