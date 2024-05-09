package com.oneinstep.starter.core.dao;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 分页参数
 **/
@ToString
@Getter
@Setter
@Schema(description = "分页参数")
public class PageOption implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 页码
     */
    @Schema(description = "页码")
    private Integer pageNo;
    /**
     * 每页条数
     */
    @Schema(description = "每页条数")
    private Integer pageSize;
    /**
     * 排序字段
     */
    @Schema(description = "排序字段")
    private String order;
    /**
     * 排序方式
     */
    @Schema(description = "排序方式")
    private String orderBy;

    public PageData<?> toPageData() {
        PageData<?> pageData = new PageData<>();
        pageData.setPageNo(this.pageNo == null || this.pageNo <= 0 ? 1 : this.pageNo);
        pageData.setPageSize(this.pageSize == null || this.pageSize <= 0 ? 10 : this.pageSize);
        pageData.setOrder(this.order);
        pageData.setOrderBy(this.orderBy);

        pageData.setNeedData(true);
        pageData.setNeedTotalCount(true);
        return pageData;
    }
}
