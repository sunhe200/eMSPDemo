package com.example.emspdemo.domain.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageResponse<T> extends BaseResponse<Page<T>> {

    @Serial
    private static final long serialVersionUID = 6806782707339799790L;

    public PageResponse() {
    }

    public PageResponse(String code) {
        super(code);
    }

    public PageResponse(String code, String msg) {
        super(code, msg);
    }

    public PageResponse(String code, String msg, Page<T> data) {
        super(code, msg, data);
    }

    public static  <T> PageResponse<T> success(Page<T> data){
        PageResponse<T> response = new PageResponse<>(DEFAULT_SUCCESS_CODE);
        response.setData(data);
        return response;
    }
}
