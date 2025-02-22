package com.example.emspdemo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.emspdemo.domain.dto.AccountCardDTO;
import com.example.emspdemo.domain.dto.AccountCardQueryDTO;

public interface AccountCardService {
    /**
     * 按 last_updated 时间范围分页查询 Account 与对应 Card 信息
     * @param query 检索条件
     * @param page 页码（从 1 开始）
     * @param size 每页记录数
     * @return 分页结果，包含 AccountTokenDTO 列表
     */
    Page<AccountCardDTO> getAccountWithCardByLastUpdated(AccountCardQueryDTO query, int page, int size);
}
