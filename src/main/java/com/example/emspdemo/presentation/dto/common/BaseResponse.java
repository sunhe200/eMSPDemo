package com.example.emspdemo.presentation.dto.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class BaseResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -7392568452838729331L;

    // 默认成功code
    protected static final String DEFAULT_SUCCESS_CODE = "0";

    // 返回码
    private String code;

    // 返回消息
    private String msg;

    // 返回数据
    private T data;

    public  boolean isSuccess() {
        return DEFAULT_SUCCESS_CODE.equals(code);
    }

    public BaseResponse() {
    }

    public BaseResponse(String code) {
        this.code = code;
    }

    public BaseResponse(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BaseResponse(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> BaseResponse<T> success(T data) {
        BaseResponse<T> response = new BaseResponse<>(DEFAULT_SUCCESS_CODE);
        response.setData(data);
        return response;
    }

    public static <T> BaseResponse<T> success() {
        return new BaseResponse<>(DEFAULT_SUCCESS_CODE);
    }
}
