package com.example.emspdemo.controller;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.emspdemo.domain.common.BaseRequest;
import com.example.emspdemo.domain.common.BaseResponse;
import com.example.emspdemo.domain.common.PageRequest;
import com.example.emspdemo.domain.common.PageResponse;
import com.example.emspdemo.domain.dto.AccountCardDTO;
import com.example.emspdemo.domain.dto.AccountCardQueryDTO;
import com.example.emspdemo.domain.dto.AccountDTO;
import com.example.emspdemo.domain.enums.AccountStatus;
import com.example.emspdemo.service.AccountCardService;
import com.example.emspdemo.service.AccountService;
import com.example.emspdemo.util.EmailUtil;
import com.example.emspdemo.util.TraceHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@Slf4j
public class AccountController {
    private final AccountService accountService;
    private final AccountCardService accountCardService;

    public AccountController(AccountService accountService, AccountCardService accountCardService) {
        this.accountService = accountService;
        this.accountCardService = accountCardService;
    }

    @PostMapping("/")
    public BaseResponse<Void> createAccount(@RequestBody BaseRequest<AccountDTO> request) {
        log.info("Create Account Start - TraceID: {} - parameter：{}", TraceHolder.get(), request);
        Assert.notNull(request, "Parameter is NULL！");
        Assert.notNull(request.getRequestData(), "Parameter is NULL！");
        accountService.createAccount(request.getRequestData());
        log.info("Create Account End - TraceID: {}", TraceHolder.get());
        return BaseResponse.success();
    }

    @PutMapping("/{id}/status")
    public BaseResponse<Void> changeAccountStatus(
            @PathVariable Long id, @RequestParam AccountStatus status, @RequestParam String editor) {
        log.info("Change status of Account Start - TraceID: {} - Account ID：{}, Account Status: {}, Editor: {}", TraceHolder.get(), id, status, editor);
        Assert.notNull(id, "Account ID is NUll!");
        Assert.notNull(status, "Account Status is NUll!");
        Assert.notNull(editor, "Editor is NULL！");
        accountService.changeAccountStatus(id, status, editor);
        log.info("Change status of Account End - TraceID: {}", TraceHolder.get());
        return BaseResponse.success();
    }

    @PostMapping("/query")
    public PageResponse<AccountCardDTO> getAccountWithCard(@RequestBody PageRequest<AccountCardQueryDTO> request) {
        log.info("Query Account With Card Start - TraceID: {} - Parameter：{}", TraceHolder.get(), request);
        Assert.notNull(request, "Account ID is NUll!");
        Page<AccountCardDTO> res = accountCardService.getAccountWithCardByLastUpdated(request.getRequestData(), request.getPageNo(), request.getPageSize());
        log.info("Query Account With Card End - TraceID: {}", TraceHolder.get());
        return PageResponse.success(res);
    }

}
