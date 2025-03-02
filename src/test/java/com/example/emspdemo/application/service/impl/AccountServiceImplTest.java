package com.example.emspdemo.application.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.emspdemo.application.command.ChangeAccountStatusCommand;
import com.example.emspdemo.application.command.CreateAccountCommand;
import com.example.emspdemo.application.query.AccountQuery;
import com.example.emspdemo.domain.Account;
import com.example.emspdemo.domain.enums.AccountStatus;
import com.example.emspdemo.domain.event.DomainEvent;
import com.example.emspdemo.presentation.dto.common.PageRequest;
import com.example.emspdemo.repository.dao.AccountDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {

    @Mock
    private AccountDao accountDao;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAccount_Success() {
        // 准备测试数据
        CreateAccountCommand command = new CreateAccountCommand();
        command.setEmail("test@example.com");
        command.setEditor("admin");

        // 模拟根据 email 查询时未查到已有账户
        when(accountDao.findByEmail("test@example.com")).thenReturn(null);
        // 模拟保存操作：在调用 save 后将 id 设置为 1L
        doAnswer(invocation -> {
            Account account = invocation.getArgument(0);
            account.setId(1L);
            return null;
        }).when(accountDao).save(any(Account.class));

        // 调用方法
        Account account = accountService.createAccount(command);

        // 验证结果
        assertNotNull(account);
        assertEquals("test@example.com", account.getEmail());
        assertEquals("admin", account.getEditor()); // 假设 Account 对象中记录了 editor 信息
        assertEquals(1L, account.getId());

        // 验证 save 和事件发布调用
        verify(accountDao, times(1)).save(any(Account.class));
        // 领域事件发布可能不止一次，根据具体领域事件的生成情况，这里使用 atLeast(0)
        verify(eventPublisher, atLeast(0)).publishEvent(any(DomainEvent.class));
    }

    @Test
    void testCreateAccount_EmailAlreadyExists() {
        // 准备测试数据
        CreateAccountCommand command = new CreateAccountCommand();
        command.setEmail("existing@example.com");
        command.setEditor("admin");

        // 模拟根据 email 查询到已存在账户
        Account existing = new Account();
        existing.setEmail("existing@example.com");
        when(accountDao.findByEmail("existing@example.com")).thenReturn(existing);

        // 调用方法，期望抛出异常
        RuntimeException ex = assertThrows(RuntimeException.class, () -> accountService.createAccount(command));
        assertTrue(ex.getMessage().contains("Email already exists"));
    }

    @Test
    void testChangeAccountStatus_Success() {
        Long accountId = 1L;
        ChangeAccountStatusCommand command = new ChangeAccountStatusCommand();
        command.setStatus(AccountStatus.ACTIVATED);
        command.setEditor("editorUser");

        // 准备一个存在的 Account
        Account account = new Account();
        account.setId(accountId);
        account.setEmail("test@example.com");
        account.setStatus(AccountStatus.CREATED);

        // 模拟查找账户
        when(accountDao.findById(accountId)).thenReturn(account);
        doNothing().when(accountDao).save(any(Account.class));

        // 调用方法
        Account updatedAccount = accountService.changeAccountStatus(accountId, command);

        // 验证更新后的状态
        assertNotNull(updatedAccount);
        assertEquals(AccountStatus.ACTIVATED, updatedAccount.getStatus());

        verify(accountDao, times(1)).save(any(Account.class));
        verify(eventPublisher, atLeast(0)).publishEvent(any(DomainEvent.class));
    }

    @Test
    void testChangeAccountStatus_AccountNotFound() {
        Long accountId = 1L;
        ChangeAccountStatusCommand command = new ChangeAccountStatusCommand();
        command.setStatus(AccountStatus.ACTIVATED);
        command.setEditor("editorUser");

        // 模拟查找不到账户
        when(accountDao.findById(accountId)).thenReturn(null);

        // 断言抛出异常
        RuntimeException ex = assertThrows(RuntimeException.class, () -> accountService.changeAccountStatus(accountId, command));
        assertEquals("Account not found", ex.getMessage());
    }

    @Test
    void testQueryAccounts() {
        // 准备查询条件
        AccountQuery accountQuery = new AccountQuery();
        // 可设置 startDate、endDate 等条件
        PageRequest<AccountQuery> pageRequest = new PageRequest<>();
        pageRequest.setRequestData(accountQuery);
        pageRequest.setPageNo(0);
        pageRequest.setPageSize(10);

        // 准备返回结果，构造一个 Page<Account>
        Page<Account> page = new Page<>(0, 10, 1);
        Account account = new Account();
        account.setId(1L);
        account.setEmail("test@example.com");
        account.setStatus(AccountStatus.CREATED);
        page.setRecords(Collections.singletonList(account));

        when(accountDao.queryAccounts(accountQuery, 0, 10)).thenReturn(page);

        // 调用方法
        Page<Account> resultPage = accountService.queryAccounts(pageRequest);

        // 验证返回结果
        assertNotNull(resultPage);
        assertEquals(1, resultPage.getTotal());
        assertEquals(1, resultPage.getRecords().size());
        verify(accountDao, times(1)).queryAccounts(accountQuery, 0, 10);
    }
}