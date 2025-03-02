package com.example.emspdemo.domain;

import com.example.emspdemo.domain.enums.CardStatus;
import com.example.emspdemo.domain.enums.TokenProp;
import com.example.emspdemo.domain.enums.TokenType;
import com.example.emspdemo.domain.event.CardAssignedEvent;
import com.example.emspdemo.domain.event.CardCreatedEvent;
import com.example.emspdemo.domain.event.CardStatusChangedEvent;
import com.example.emspdemo.domain.event.DomainEvent;
import com.example.emspdemo.domain.vo.Token;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void testCreate() {
        String editor = "admin";
        // 调用工厂方法创建 Card
        Card card = Card.create(editor);
        assertNotNull(card, "Card should not be null");
        // 验证初始状态
        assertEquals(CardStatus.CREATED, card.getStatus(), "Card status should be CREATED");
        assertEquals(editor, card.getEditor(), "Editor should match the provided value");
        assertNotNull(card.getLastUpdated(), "lastUpdated should not be null");
        // 验证 tokens：应包含两个 token，分别对应 UID 和 visibleNumber
        List<Token> tokens = card.getTokens();
        assertNotNull(tokens, "Tokens list should not be null");
        assertEquals(2, tokens.size(), "Tokens list size should be 2");
        boolean hasUid = tokens.stream().anyMatch(token -> token.getPropName() == TokenProp.UID);
        boolean hasVisible = tokens.stream().anyMatch(token -> token.getPropName() == TokenProp.VISIBLE_NUMBER);
        assertTrue(hasUid, "Tokens should contain UID token");
        assertTrue(hasVisible, "Tokens should contain VISIBLE_NUMBER token");
        // 验证领域事件：创建时触发 CardCreatedEvent
        List<DomainEvent> events = card.getDomainEvents();
        assertNotNull(events, "Domain events list should not be null");
        assertFalse(events.isEmpty(), "There should be at least one domain event");
        DomainEvent firstEvent = events.get(0);
        assertTrue(firstEvent instanceof CardCreatedEvent, "First domain event should be CardCreatedEvent");
    }

    @Test
    void testAssignToAccount() throws InterruptedException {
        // 先创建 Card
        Card card = Card.create("admin");
        // 记录修改前的 lastUpdated 时间
        Date before = card.getLastUpdated();
        // 稍等以保证时间变化
        Thread.sleep(10);
        Long accountId = 100L;
        String assignEditor = "assigner";
        card.assignToAccount(accountId, assignEditor);
        // 验证 accountId 设置
        assertEquals(accountId, card.getAccountId(), "AccountId should be set");
        // 验证 editor 更新
        assertEquals(assignEditor, card.getEditor(), "Editor should be updated");
        // 验证 lastUpdated 更新
        assertTrue(card.getLastUpdated().after(before), "lastUpdated should be updated");
        // 验证领域事件中存在 CardAssignedEvent
        List<DomainEvent> events = card.getDomainEvents();
        boolean foundAssignedEvent = events.stream().anyMatch(e -> e instanceof CardAssignedEvent);
        assertTrue(foundAssignedEvent, "Domain events should contain CardAssignedEvent");
    }

    @Test
    void testChangeStatus() throws InterruptedException {
        // 创建 Card
        Card card = Card.create("admin");
        Date before = card.getLastUpdated();
        // 稍作等待，确保时间变化
        Thread.sleep(10);
        String newEditor = "operator";
        card.changeStatus(CardStatus.ACTIVATED, newEditor);
        // 验证状态更新
        assertEquals(CardStatus.ACTIVATED, card.getStatus(), "Card status should be updated to ACTIVATED");
        // 验证 editor 更新
        assertEquals(newEditor, card.getEditor(), "Editor should be updated");
        // 验证 lastUpdated 更新
        assertTrue(card.getLastUpdated().after(before), "lastUpdated should be updated after status change");
        // 验证领域事件中包含 CardStatusChangedEvent
        List<DomainEvent> events = card.getDomainEvents();
        boolean foundStatusChanged = events.stream().anyMatch(e -> e instanceof CardStatusChangedEvent);
        assertTrue(foundStatusChanged, "Domain events should contain CardStatusChangedEvent");
    }

    @Test
    void testClearDomainEvents() {
        // 创建 Card 后，领域事件列表应不为空
        Card card = Card.create("admin");
        assertFalse(card.getDomainEvents().isEmpty(), "Domain events should not be empty after creation");
        // 调用 clearDomainEvents 方法后，列表应为空
        card.clearDomainEvents();
        assertTrue(card.getDomainEvents().isEmpty(), "Domain events should be cleared");
    }
}
