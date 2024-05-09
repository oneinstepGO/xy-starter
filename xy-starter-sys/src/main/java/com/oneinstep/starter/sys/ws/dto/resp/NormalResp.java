package com.oneinstep.starter.sys.ws.dto.resp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : hahaha
 * @created : 2023/12/27
 * @since: 1.0.0
 **/
@Getter
@Setter
@ToString
public class NormalResp implements Serializable {

    public static final int SUCCESS = 200;
    public static final int ERROR = -1;
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer code;

    private String msg;

    private Long timestamp;

}
