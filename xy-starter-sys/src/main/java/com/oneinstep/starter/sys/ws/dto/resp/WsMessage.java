package com.oneinstep.starter.sys.ws.dto.resp;

import com.oneinstep.starter.sys.enums.ws.WsMsgTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author : hahaha
 * @created : 2023/12/27
 * @since: 1.0.0
 **/
@Getter
@Setter
@ToString
public class WsMessage<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * {
     *     "id": 10001000,
     *     "timestamp": 1703647711114,
     *     "msgType": 1,
     *     "content": [
     *
     *     ]
     * }
     */
    /**
     * 消息id
     */
    private Long id;
    /**
     * 时间戳
     */
    private Long timestamp;
    /**
     * 消息类型
     *
     * @see WsMsgTypeEnum
     */
    private Integer msgType;
    /**
     * 消息内容
     */
    private List<T> content;
}
