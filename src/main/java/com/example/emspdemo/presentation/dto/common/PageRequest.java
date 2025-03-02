package com.example.emspdemo.presentation.dto.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageRequest<T> extends BaseRequest<T> {
    @Serial
    private static final long serialVersionUID = -8511572357069203947L;

    // 当前页数,默认起始页1
    private int pageNo = 1;
    // 每页大小
    private int pageSize = 20;

    public PageRequest() {
    }

    public PageRequest(T requestData) {
        super(requestData);
    }
}
