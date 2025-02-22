package com.example.emspdemo.component;

import com.example.emspdemo.domain.common.BaseResponse;
import com.example.emspdemo.domain.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 统一全局异常处理
 *
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public BaseResponse<?> illegalArgumentException(Exception e) {
        log.error(e.getMessage(), e);
        return new BaseResponse<>(ErrorCode.ARGUMENT_ERROR.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeResponse(RuntimeException e) {
        log.error(e.getMessage(), e);
        return new BaseResponse<>(ErrorCode.SYSTEM_ERROR.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public BaseResponse<?> exception(Exception e) {
        log.error(e.getMessage(), e);
        return new BaseResponse<>(ErrorCode.SYSTEM_ERROR.getCode(), ErrorCode.SYSTEM_ERROR.getMessage());
    }
}
