package com.example.emspdemo.component;

import com.example.emspdemo.domain.enums.ErrorCode;
import com.example.emspdemo.presentation.dto.common.BaseResponse;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    public static class Dummy {
        public void dummyMethod(String param) {
        }
    }

    @Test
    void testIllegalArgumentExceptionHandler() {
        IllegalArgumentException ex = new IllegalArgumentException("Test illegal argument");
        ResponseEntity<BaseResponse<?>> response = handler.illegalArgumentException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        BaseResponse<?> body = response.getBody();
        assertNotNull(body);
        assertEquals(ErrorCode.ARGUMENT_ERROR.getCode(), body.getCode());
        assertTrue(body.getMsg().contains("Test illegal argument"));
    }

    @Test
    void testMethodArgumentNotValidExceptionHandler() throws NoSuchMethodException {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "target");
        bindingResult.addError(new FieldError("target", "field", "Field must not be blank"));

        Method dummyMethod = Dummy.class.getMethod("dummyMethod", String.class);
        MethodParameter methodParameter = new MethodParameter(dummyMethod, 0);

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<BaseResponse<?>> response = handler.handleMethodArgumentNotValid(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        BaseResponse<?> body = response.getBody();
        assertNotNull(body);
        // 错误信息应包含 FieldError 的消息
        assertTrue(body.getMsg().contains("Field must not be blank"));
        assertEquals(ErrorCode.ARGUMENT_ERROR.getCode(), body.getCode());
    }

    @Test
    void testConstraintViolationExceptionHandler() {
        // 模拟 ConstraintViolation 对象
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        // 模拟 Path 对象
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("field");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be null");

        Set<ConstraintViolation<Object>> violations = new HashSet<>();
        violations.add(violation);

        ConstraintViolationException ex = new ConstraintViolationException(violations);

        ResponseEntity<BaseResponse<?>> response = handler.handleConstraintViolation(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        BaseResponse<?> body = response.getBody();
        assertNotNull(body);
        // 拼接的错误信息应包含 "field must not be null"
        assertTrue(body.getMsg().contains("field must not be null"));
        assertEquals(ErrorCode.ARGUMENT_ERROR.getCode(), body.getCode());
    }

    @Test
    void testHttpMessageNotReadableExceptionHandler_withMismatchedInput() {
        // 创建一个 MismatchedInputException（假设 Jackson 版本支持该静态方法）
        MismatchedInputException mie = MismatchedInputException.from(null, String.class, "Specific error message");
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("HTTP message not readable", mie);
        ResponseEntity<BaseResponse<?>> response = handler.handleHttpMessageNotReadable(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        BaseResponse<?> body = response.getBody();
        assertNotNull(body);
        // 应返回 MismatchedInputException 中的原始错误信息
        assertTrue(body.getMsg().contains("Specific error message"));
        assertEquals(ErrorCode.ARGUMENT_ERROR.getCode(), body.getCode());
    }

    @Test
    void testHttpMessageNotReadableExceptionHandler_withoutCause() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("HTTP message not readable");
        ResponseEntity<BaseResponse<?>> response = handler.handleHttpMessageNotReadable(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        BaseResponse<?> body = response.getBody();
        assertNotNull(body);
        // 当没有 cause 时，返回默认错误消息
        assertEquals("Invalid request payload", body.getMsg());
        assertEquals(ErrorCode.ARGUMENT_ERROR.getCode(), body.getCode());
    }

    @Test
    void testRuntimeExceptionHandler() {
        RuntimeException ex = new RuntimeException("Runtime error occurred");
        ResponseEntity<BaseResponse<?>> response = handler.runtimeResponse(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        BaseResponse<?> body = response.getBody();
        assertNotNull(body);
        assertEquals(ErrorCode.SYSTEM_ERROR.getCode(), body.getCode());
        assertTrue(body.getMsg().contains("Runtime error occurred"));
    }

    @Test
    void testGenericExceptionHandler() {
        Exception ex = new Exception("Generic error");
        ResponseEntity<BaseResponse<?>> response = handler.exception(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        BaseResponse<?> body = response.getBody();
        assertNotNull(body);
        // 此处返回的是 ErrorCode.SYSTEM_ERROR.getMessage()
        assertEquals(ErrorCode.SYSTEM_ERROR.getMessage(), body.getMsg());
        assertEquals(ErrorCode.SYSTEM_ERROR.getCode(), body.getCode());
    }
}
