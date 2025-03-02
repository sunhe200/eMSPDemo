package com.example.emspdemo.presentation.dto.common;

import cn.hutool.core.util.IdUtil;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;


@Data
public class BaseRequest<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -518898040392999642L;

    // 请求流水号
    private String requestNo = IdUtil.fastSimpleUUID();
    // 请求时间
    private Date requestTime;
    // 请求数据
    @NotNull(message = "Request Data cannot be null")
    private T requestData;
    // 扩展字段
    private Map<String, Object> ext;

    public BaseRequest() {
        requestNo = IdUtil.fastSimpleUUID();
    }

    public BaseRequest(T requestData) {
        this.requestData = requestData;
        requestNo = IdUtil.fastSimpleUUID();
    }

}
