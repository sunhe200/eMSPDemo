package com.example.emspdemo.presentation.controller;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.emspdemo.application.command.ChangeAccountStatusCommand;
import com.example.emspdemo.application.command.CreateAccountCommand;
import com.example.emspdemo.application.query.AccountQuery;
import com.example.emspdemo.assembler.AccountAssembler;
import com.example.emspdemo.domain.Account;
import com.example.emspdemo.presentation.dto.common.BaseRequest;
import com.example.emspdemo.presentation.dto.common.BaseResponse;
import com.example.emspdemo.presentation.dto.common.PageRequest;
import com.example.emspdemo.presentation.dto.common.PageResponse;
import com.example.emspdemo.presentation.dto.AccountDTO;
import com.example.emspdemo.application.service.AccountService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@Slf4j
@Validated
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse<AccountDTO> createAccount(@Valid @RequestBody BaseRequest<CreateAccountCommand> request) {
        log.info("Create Account Start - parameter：{}", request);
        Assert.notNull(request, "Parameter is NULL！");
        Assert.notNull(request.getRequestData(), "Parameter is NULL！");
        Account account = accountService.createAccount(request.getRequestData());
        AccountDTO accountDTO = AccountAssembler.toDTO(account);
        log.info("Create Account End - RequestNo: {}", request.getRequestNo());
        return BaseResponse.success(accountDTO);
    }

    @PatchMapping("/{id}/status")
    public BaseResponse<AccountDTO > changeAccountStatus(
            @PathVariable Long id, @Valid @RequestBody BaseRequest<ChangeAccountStatusCommand> request) {
        log.info("Change status of Account Start - Account ID：{}, parameter: {}", id, request);
        Assert.notNull(request, "Parameter is NULL！");
        Assert.notNull(request.getRequestData(), "Parameter is NULL！");
        Account account = accountService.changeAccountStatus(id, request.getRequestData());
        AccountDTO accountDTO = AccountAssembler.toDTO(account);
        log.info("Change status of Account End - RequestNo: {}", request.getRequestNo());
        return BaseResponse.success(accountDTO);
    }

    @PostMapping("/query")
    public PageResponse<AccountDTO> getAccountWithCard(@Valid @RequestBody PageRequest<AccountQuery> request) {
        log.info("Query Account With Card Start - Parameter：{}", request);
        Assert.notNull(request, "Parameter is NUll!");
        Assert.notNull(request.getRequestData(), "Parameter is NULL！");
        Page<Account> accountPage = accountService.queryAccounts(request);
        Page<AccountDTO> dtoPage = new Page<>(accountPage.getCurrent(), accountPage.getSize(), accountPage.getTotal());
        dtoPage.setRecords(accountPage.getRecords().stream().map(AccountAssembler::toDTO).toList());
        log.info("Query Account With Card End - RequestNo: {}", request.getRequestNo());
        return PageResponse.success(dtoPage);
    }

}
