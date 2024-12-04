package com.oneinstep.starter.core.exception;

import com.oneinstep.starter.core.error.BaseCodeAndMsgError;
import com.oneinstep.starter.core.response.Result;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Restful 异常处理
 */
@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    /**
     * 处理请求类型错误
     *
     * @return BAD_REQUEST
     */
    @Order(0)
    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleHttpMediaTypeNotSupportedException() {
        return Result.error(BaseCodeAndMsgError.METHOD_NOT_ALLOWED);
    }

    @Order(1)
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public Result<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("request method not supported: {}", e.getMessage());
        return Result.error(BaseCodeAndMsgError.METHOD_NOT_ALLOWED);
    }

    @Order(2)
    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        StringBuilder sb = new StringBuilder("参数错误：[");
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        Iterator<ConstraintViolation<?>> iterator = violations.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            ConstraintViolation<?> c = iterator.next();
            sb.append(c.getMessage());
            if (i++ != violations.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(']');
        String msg = sb.toString();
        return Result.error(BaseCodeAndMsgError.ILLEGAL_ARGUMENT.getCode(), msg);
    }

    /**
     * 处理参数校验（基于注解）失败
     *
     * @param e 参数校验失败异常
     * @return 400
     */
    @Order(3)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        StringBuilder sb = new StringBuilder("参数错误：[");
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        int i = 0;
        for (ObjectError error : errors) {
            sb.append(error.getDefaultMessage());
            if (i++ != errors.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(']');
        String msg = sb.toString();
        return Result.error(BaseCodeAndMsgError.ILLEGAL_ARGUMENT.getCode(), msg);
    }

    /**
     * 处理参数非法
     *
     * @param e 参数非法异常
     * @return 400
     */
    @Order(4)
    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error(e.getMessage(), e);
        return Result.error(BaseCodeAndMsgError.ILLEGAL_ARGUMENT.getCode(), e.getMessage());
    }

    /**
     * 处理 OneBaseException
     *
     * @param e OneBaseException
     * @return FAILURE
     */
    @Order(5)
    @ExceptionHandler({OneBaseException.class})
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleOneBaseException(OneBaseException e) {
        log.error(e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

//    /**
//     * 处理 Throwable
//     *
//     * @param e Throwable 异常
//     * @return FAILURE
//     */
//    @Order(1000000)
//    @ExceptionHandler({RuntimeException.class, Exception.class, Throwable.class})
//    @ResponseStatus(HttpStatus.OK)
//    public Result<Void> handleThrowable(Throwable e) {
//        log.error(e.getMessage(), e);
//        return Result.error(BaseCodeAndMsgError.FAILURE);
//    }

}
