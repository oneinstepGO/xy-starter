package com.oneinstep.starter.sys.ws.dto.req;

import com.oneinstep.starter.sys.enums.ws.WsClientMsgTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * ws客户端请求消息
 **/
@Getter
@Setter
@ToString
public class WsClientReqMsg<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 消息类型
     *
     * @see WsClientMsgTypeEnum
     */
    private String action;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 时间戳
     */
    private Long timestamp;
    /**
     * 消息内容
     */
    private T data;
}
