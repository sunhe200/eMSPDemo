package com.example.emspdemo.service;

import com.example.emspdemo.domain.dto.AccountDTO;
import com.example.emspdemo.domain.enums.AccountStatus;

public interface AccountService {
    /**
     * 创建账户。
     */
    void createAccount(AccountDTO accountDTO);

    /**
     * 修改账户状态。
     */
    void changeAccountStatus(Long accountId, AccountStatus status, String editor);

}
