package com.example.emspdemo.application.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.emspdemo.application.command.ChangeAccountStatusCommand;
import com.example.emspdemo.application.command.CreateAccountCommand;
import com.example.emspdemo.application.query.AccountQuery;
import com.example.emspdemo.application.service.AccountService;
import com.example.emspdemo.domain.Account;
import com.example.emspdemo.domain.event.DomainEvent;
import com.example.emspdemo.presentation.dto.common.PageRequest;
import com.example.emspdemo.repository.dao.AccountDao;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountDao accountDao;
    private final ApplicationEventPublisher eventPublisher;

    public AccountServiceImpl(AccountDao accountDao, ApplicationEventPublisher eventPublisher) {
        this.accountDao = accountDao;
        this.eventPublisher = eventPublisher;
    }


    @Override
    @Transactional
    public Account createAccount(CreateAccountCommand command) {
        Account existingAccount = accountDao.findByEmail(command.getEmail());
        if (existingAccount != null) {
            throw new RuntimeException("Email already exists: " + command.getEmail());
        }
        // 调用领域层的工厂方法生成 Account 聚合根，
        Account account = Account.create(command.getEmail(), command.getEditor());
        // 保存聚合根
        accountDao.save(account);
        // 发布领域事件
        publishDomainEvents(account);
//        return accountDao.findByEmail(command.getEmail());
        return account;
    }

    @Override
    @Transactional
    public Account changeAccountStatus(Long accountId, ChangeAccountStatusCommand command) {
        Account account = accountDao.findById(accountId);
        if (account == null) {
            throw new RuntimeException("Account not found");
        }
        account.changeStatus(command.getStatus(), command.getEditor());
        accountDao.save(account);
        publishDomainEvents(account);
        return account;
    }

    @Override
    public Page<Account> queryAccounts(PageRequest<AccountQuery> query) {
        return accountDao.queryAccounts(query.getRequestData(), query.getPageNo(), query.getPageSize());
    }

    /**
     * 遍历 Account 中的领域事件，使用事件发布器发布后清空事件列表。
     */
    private void publishDomainEvents(Account account) {
        List<DomainEvent> events = account.getDomainEvents();
        if (events != null) {
            for (DomainEvent event : events) {
                eventPublisher.publishEvent(event);
            }
        }
        account.clearEvent();
    }
}

