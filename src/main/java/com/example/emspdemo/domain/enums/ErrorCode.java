package com.example.emspdemo.domain.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    /**
     * 错误类型
     */
    SYSTEM_ERROR("999999", "系统内部错误"),
    NO_AUTH_ERROR("999403", "该用户无权限"),
    ARGUMENT_ERROR("999998", "参数非法"),
    ;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private final String code;
    private final String message;

}
