package com.oneinstep.starter.core.dao;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 分页对象
 */
@Getter
@Setter
@ToString
@Schema(description = "分页对象")
public class PageData<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 页码
     */
    @Schema(description = "页码")
    private Integer pageNo;
    /**
     * 每页大小
     */
    @Schema(description = "每页大小")
    private Integer pageSize;
    /**
     * 总条数
     */
    @Schema(description = "总条数")
    private Long totalCount;
    /**
     * 是否需要总条数
     */
    @Schema(description = "是否需要总条数")
    private Boolean needTotalCount;
    /**
     * 是否需要数据
     */
    @Schema(description = "是否需要数据")
    private Boolean needData;
    /**
     * 排序字段
     */
    @Schema(description = "排序字段")
    private String order;
    /**
     * 排序方向
     */
    @Schema(description = "排序方向")
    private String orderBy;
    /**
     * 数据列表
     */
    @Schema(description = "数据列表")
    private List<T> dataList;
}
