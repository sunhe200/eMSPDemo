package com.example.emspdemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.emspdemo.domain.convert.AccountConvert;
import com.example.emspdemo.domain.dto.AccountDTO;
import com.example.emspdemo.domain.enums.AccountStatus;
import com.example.emspdemo.domain.event.AccountCreatedEvent;
import com.example.emspdemo.domain.event.AccountStatusChangedEvent;
import com.example.emspdemo.domain.po.AccountPO;
import com.example.emspdemo.repository.AccountMapper;
import com.example.emspdemo.util.EMAIDUtil;
import com.example.emspdemo.util.EmailUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * 正常创建账号流程测试：
     * 1. EmailUtil.checkEmailFormat 正常校验通过；
     * 2. EMAIDUtil.generateEMAID 与 normalizeEMAID 返回预置值；
     * 3. accountMapper.insert 与 selectOne 被调用；
     * 4. 最后发布 AccountCreatedEvent，事件中 AccountDTO 的状态和 contractId 均符合预期。
     */
    @Test
    void testCreateAccount_Success() {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setEmail("test@example.com");

        // 使用静态 Mock 模拟 EmailUtil 和 EMAIDUtil 的行为
        try (MockedStatic<EmailUtil> emailUtilMock = mockStatic(EmailUtil.class);
             MockedStatic<EMAIDUtil> emaidUtilMock = mockStatic(EMAIDUtil.class)) {

            // 模拟 EmailUtil.checkEmailFormat 正常执行（void 方法使用 thenAnswer 返回 null）
            emailUtilMock.when(() -> EmailUtil.checkEmailFormat("test@example.com"))
                    .thenAnswer(invocation -> null);

            // 模拟生成与规范化合约编号
            emaidUtilMock.when(EMAIDUtil::generateEMAID).thenReturn("dummyEMAID");
            emaidUtilMock.when(() -> EMAIDUtil.normalizeEMAID("dummyEMAID")).thenReturn("normalizedEMAID");

            // 准备 accountMapper.selectOne 返回的 AccountPO
            AccountPO accountPO = new AccountPO();
            accountPO.setId(1L);
            accountPO.setEmail("test@example.com");
            accountPO.setStatus(AccountStatus.CREATED);
            accountPO.setContractId("normalizedEMAID");
            accountPO.setLastUpdated(new Date());

            // 模拟 mapper 行为：insert 返回受影响的行数
            when(accountMapper.insert(any(AccountPO.class))).thenReturn(1);
            when(accountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(accountPO);

            // 调用创建账号方法
            accountService.createAccount(accountDTO);

            // 验证 accountMapper.insert 与 selectOne 被各调用一次
            verify(accountMapper, times(1)).insert(any(AccountPO.class));
            verify(accountMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));

            // 捕获并验证发布的 AccountCreatedEvent
            ArgumentCaptor<AccountCreatedEvent> eventCaptor = ArgumentCaptor.forClass(AccountCreatedEvent.class);
            verify(publisher, times(1)).publishEvent(eventCaptor.capture());
            AccountCreatedEvent createdEvent = eventCaptor.getValue();
            assertNotNull(createdEvent.getAccount(), "AccountCreatedEvent 中的 AccountDTO 不能为空");
            assertEquals(AccountStatus.CREATED, createdEvent.getAccount().getStatus(), "账号状态应为 CREATED");
            assertEquals("normalizedEMAID", createdEvent.getAccount().getContractId(), "合约编号应为 normalizedEMAID");
        }
    }

    /**
     * 测试创建账号时，EmailUtil 校验失败，抛出 IllegalArgumentException 异常。
     */
    @Test
    void testCreateAccount_InvalidEmail() {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setEmail("invalid-email");

        try (MockedStatic<EmailUtil> emailUtilMock = mockStatic(EmailUtil.class)) {
            emailUtilMock.when(() -> EmailUtil.checkEmailFormat("invalid-email"))
                    .thenThrow(new IllegalArgumentException("Invalid email format"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> accountService.createAccount(accountDTO));
            assertEquals("Invalid email format", exception.getMessage());
        }
    }

    /**
     * 正常修改账号状态测试：
     * 1. accountMapper.selectById 返回存在的账号；
     * 2. 修改状态、编辑者及更新时间后调用 updateById；
     * 3. 最后发布 AccountStatusChangedEvent，并验证事件中账号状态与编辑者。
     */
    @Test
    void testChangeAccountStatus_Success() {
        Long accountId = 1L;
        AccountStatus newStatus = AccountStatus.ACTIVATED; // 假设 ACTIVATED 为有效枚举值
        String editor = "testEditor";

        AccountPO accountPO = new AccountPO();
        accountPO.setId(accountId);
        accountPO.setStatus(AccountStatus.CREATED);
        accountPO.setEmail("test@example.com");

        // 模拟查询账号，先返回存在的账号，再返回更新后的账号
        when(accountMapper.selectById(accountId)).thenReturn(accountPO, accountPO);
        // updateById 返回更新行数
        when(accountMapper.updateById(accountPO)).thenReturn(1);

        // 调用修改状态方法
        accountService.changeAccountStatus(accountId, newStatus, editor);

        // 验证 updateById 被调用一次
        verify(accountMapper, times(1)).updateById(accountPO);

        // 捕获并验证发布的 AccountStatusChangedEvent
        ArgumentCaptor<AccountStatusChangedEvent> eventCaptor = ArgumentCaptor.forClass(AccountStatusChangedEvent.class);
        verify(publisher, times(1)).publishEvent(eventCaptor.capture());
        AccountStatusChangedEvent changedEvent = eventCaptor.getValue();
        assertNotNull(changedEvent.getAccount(), "AccountStatusChangedEvent 中的 AccountDTO 不能为空");
        assertEquals(newStatus, changedEvent.getAccount().getStatus(), "账号状态应被更新为 ACTIVATED");
        assertEquals(editor, changedEvent.getAccount().getEditor(), "编辑者应被更新");
    }

    /**
     * 测试修改账号状态时，账号不存在，抛出异常。
     */
    @Test
    void testChangeAccountStatus_AccountNotFound() {
        Long accountId = 1L;
        AccountStatus newStatus = AccountStatus.ACTIVATED;
        String editor = "testEditor";

        // 模拟 accountMapper.selectById 返回 null 表示账号不存在
        when(accountMapper.selectById(accountId)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountService.changeAccountStatus(accountId, newStatus, editor));
        assertEquals("Account not found", exception.getMessage());
    }
}
