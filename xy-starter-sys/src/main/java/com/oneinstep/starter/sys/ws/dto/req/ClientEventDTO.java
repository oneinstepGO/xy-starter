package com.oneinstep.starter.sys.ws.dto.req;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 客户端事件
 **/
@Getter
@Setter
@ToString
public class ClientEventDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 事件类型
     */
    private Integer eventType;
    /**
     * 事件参数
     */
    private String params;
}
