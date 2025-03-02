package com.example.emspdemo.application.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.emspdemo.application.command.ChangeAccountStatusCommand;
import com.example.emspdemo.application.command.CreateAccountCommand;
import com.example.emspdemo.application.query.AccountQuery;
import com.example.emspdemo.domain.Account;
import com.example.emspdemo.presentation.dto.common.PageRequest;


public interface AccountService {
    /**
     * 创建 Account 聚合根，自动生成 contract ID 与 EMAID token，
     * 并返回创建后的 Account（领域对象）。
     */
    Account createAccount(CreateAccountCommand command);

    /**
     * 修改指定 Account 的状态，返回更新后的 Account。
     */
    Account changeAccountStatus(Long accountId, ChangeAccountStatusCommand command);

    /**
     * 根据查询条件分页查询 Account 聚合根。
     */
    Page<Account> queryAccounts(PageRequest<AccountQuery> query);
}
