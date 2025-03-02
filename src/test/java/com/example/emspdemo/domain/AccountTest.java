package com.example.emspdemo.domain;

import com.example.emspdemo.domain.enums.AccountStatus;
import com.example.emspdemo.domain.enums.TokenProp;
import com.example.emspdemo.domain.enums.TokenType;
import com.example.emspdemo.domain.event.AccountCreatedEvent;
import com.example.emspdemo.domain.event.AccountStatusChangedEvent;
import com.example.emspdemo.domain.event.DomainEvent;
import com.example.emspdemo.domain.vo.Token;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void testCreateAccount() {
        String email = "test@example.com";
        String editor = "admin";
        // 调用工厂方法生成 Account 聚合根
        Account account = Account.create(email, editor);

        // 验证基本字段
        assertNotNull(account);
        assertEquals(email, account.getEmail());
        assertEquals(AccountStatus.CREATED, account.getStatus());
        assertNotNull(account.getLastUpdated());
        assertEquals(editor, account.getEditor());

        // 验证 token 列表：应包含一个表示合同编号的 EMAID token
        List<Token> tokens = account.getTokens();
        assertNotNull(tokens);
        assertFalse(tokens.isEmpty(), "Tokens list should not be empty");

        Token contractToken = tokens.stream()
                .filter(token -> token.getPropName() == TokenProp.CONTRACT_ID)
                .findFirst()
                .orElse(null);
        assertNotNull(contractToken, "Contract token should be present");
        assertEquals(TokenType.EMAID, contractToken.getTokenType());
        assertNotNull(contractToken.getPropValue(), "Contract token value should not be null");

        // 验证领域事件：创建方法中应触发 AccountCreatedEvent
        List<DomainEvent> events = account.getDomainEvents();
        assertNotNull(events);
        assertFalse(events.isEmpty(), "Domain events list should not be empty");
        DomainEvent event = events.get(0);
        assertTrue(event instanceof AccountCreatedEvent, "First domain event should be AccountCreatedEvent");
    }

    @Test
    void testChangeStatus() throws InterruptedException {
        // 创建 Account 作为测试对象
        String email = "test@example.com";
        String editor = "admin";
        Account account = Account.create(email, editor);
        Date initialUpdate = account.getLastUpdated();

        // 稍作等待确保时间变化（仅为测试时间变化）
        Thread.sleep(10);

        // 修改状态
        String newEditor = "operator";
        account.changeStatus(AccountStatus.ACTIVATED, newEditor);

        // 验证状态、editor 以及更新时间更新
        assertEquals(AccountStatus.ACTIVATED, account.getStatus());
        assertEquals(newEditor, account.getEditor());
        assertTrue(account.getLastUpdated().after(initialUpdate), "lastUpdated should be updated");

        // 验证领域事件中包含状态变更事件
        List<DomainEvent> events = account.getDomainEvents();
        boolean foundStatusChange = events.stream().anyMatch(e -> e instanceof AccountStatusChangedEvent);
        assertTrue(foundStatusChange, "Domain events should contain AccountStatusChangedEvent");
    }

    @Test
    void testClearEvent() {
        // 创建 Account 后，领域事件列表不为空
        Account account = Account.create("test@example.com", "admin");
        assertFalse(account.getDomainEvents().isEmpty(), "Domain events should not be empty after creation");

        // 调用 clearEvent，清空领域事件列表
        account.clearEvent();
        assertTrue(account.getDomainEvents().isEmpty(), "Domain events should be cleared");
    }
}
