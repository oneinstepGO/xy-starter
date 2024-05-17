package com.oneinstep.starter.core.response;

import com.oneinstep.starter.core.constants.CommonConstant;
import com.oneinstep.starter.core.error.BaseCodeAndMsgError;
import com.oneinstep.starter.core.error.CodeAndMsgResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.MDC;

import java.io.Serial;
import java.io.Serializable;


/**
 * 统一返回对象
 *
 * @param <T>
 */
@ToString
@Getter
@Setter
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 返回码
     */
    @Schema(description = "返回码")
    private String code;

    /**
     * 三方返回码
     */
    private String outerCode;
    /**
     * 三方返回信息
     */
    private String outMsg;

    /**
     * 返回信息
     */
    @Schema(description = "返回信息")
    private String message;

    /**
     * 返回数据
     */
    @Schema(description = "返回数据")
    private T data;

    /**
     * 请求id
     */
    private String reqId;

    /**
     * 签名
     */
    private String sign;

    /**
     * 构建成功状态，包含data的resp
     *
     * @param data 回包数据
     * @param <T>  回包数据类型
     * @return resp
     */
    public static <T> Result<T> ok(T data) {
        Result<T> response = new Result<>();
        response.setCode(BaseCodeAndMsgError.SUCCESS.getCode());
        response.setMessage(BaseCodeAndMsgError.SUCCESS.getMessage());
        response.setData(data);
        response.setReqId(MDC.get(CommonConstant.REQUEST_ID));

        return response;
    }

    public static <T> Result<T> ok() {
        return ok(null);
    }

    /**
     * 失败对象建造方法
     **/
    public static <T> Result<T> error() {
        return error(BaseCodeAndMsgError.FAILURE);
    }

    /**
     * 失败对象建造方法
     **/
    public static <T> Result<T> error(String message) {
        return error(BaseCodeAndMsgError.FAILURE.getCode(), message);
    }

    public static <T> Result<T> error(CodeAndMsgResponse responseCode) {
        return error(responseCode.getCode(), responseCode.getMessage());
    }

    public static <T> Result<T> error(CodeAndMsgResponse response, String outerCode, String outMsg) {
        Result<T> error = error(response);
        error.setOuterCode(outerCode);
        error.setOutMsg(outMsg);
        return error;
    }

    public static <T> Result<T> error(CodeAndMsgResponse response, String newMsg) {
        return error(response.getCode(), newMsg);
    }

    public static <T> Result<T> error(String code, String message) {
        Result<T> response = new Result<>();
        response.setCode(code);
        response.setMessage(message);
        response.setReqId(MDC.get(CommonConstant.REQUEST_ID));
        return response;
    }

    public boolean isSuccess() {
        return BaseCodeAndMsgError.SUCCESS.getCode().equals(this.getCode());
    }

}