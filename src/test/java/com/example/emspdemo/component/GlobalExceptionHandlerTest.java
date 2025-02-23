package com.example.emspdemo.component;

import com.example.emspdemo.domain.common.BaseResponse;
import com.example.emspdemo.domain.enums.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    public void testIllegalArgumentExceptionHandler() {
        String errorMsg = "illegal argument error";
        IllegalArgumentException ex = new IllegalArgumentException(errorMsg);
        BaseResponse<?> response = exceptionHandler.illegalArgumentException(ex);
        // 验证返回的错误码与错误信息
        assertEquals(ErrorCode.ARGUMENT_ERROR.getCode(), response.getCode());
        assertEquals(errorMsg, response.getMsg());
    }

    @Test
    public void testIllegalStateExceptionHandler() {
        String errorMsg = "illegal state error";
        IllegalStateException ex = new IllegalStateException(errorMsg);
        BaseResponse<?> response = exceptionHandler.illegalArgumentException(ex);
        // 同样属于 IllegalArgumentException 处理范围
        assertEquals(ErrorCode.ARGUMENT_ERROR.getCode(), response.getCode());
        assertEquals(errorMsg, response.getMsg());
    }

    @Test
    public void testRuntimeExceptionHandler() {
        String errorMsg = "runtime error";
        RuntimeException ex = new RuntimeException(errorMsg);
        BaseResponse<?> response = exceptionHandler.runtimeResponse(ex);
        assertEquals(ErrorCode.SYSTEM_ERROR.getCode(), response.getCode());
        assertEquals(errorMsg, response.getMsg());
    }

    @Test
    public void testExceptionHandler() {
        String errorMsg = "generic error";
        Exception ex = new Exception(errorMsg);
        BaseResponse<?> response = exceptionHandler.exception(ex);
        // 对于 Exception，返回的消息为 ErrorCode.SYSTEM_ERROR 对应的提示信息
        assertEquals(ErrorCode.SYSTEM_ERROR.getCode(), response.getCode());
        assertEquals(ErrorCode.SYSTEM_ERROR.getMessage(), response.getMsg());
    }
}
