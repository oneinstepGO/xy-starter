package com.oneinstep.starter.sys.ws.dto.resp;

import com.oneinstep.starter.sys.enums.ws.DataTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * ws 数据变更消息
 **/
@Getter
@Setter
@ToString
public class DataChangeMsg implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据类型
     *
     * @see DataTypeEnum
     */
    private Integer dataType;
    /**
     * 数据
     */
    private List<Serializable> dataList;

}
