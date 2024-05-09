package com.oneinstep.starter.sys.ws.dto.req;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 消息已读
 **/
@Getter
@Setter
@ToString
public class AckMsgDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 消息
     */
    private Long msgId;
}
