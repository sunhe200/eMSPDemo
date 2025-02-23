package com.example.emspdemo.component;

import com.example.emspdemo.domain.dto.AccountDTO;
import com.example.emspdemo.domain.dto.CardDTO;
import com.example.emspdemo.domain.event.AccountCreatedEvent;
import com.example.emspdemo.domain.event.AccountStatusChangedEvent;
import com.example.emspdemo.domain.event.CardAssignedEvent;
import com.example.emspdemo.domain.event.CardStatusChangedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class EventListenerTest {

    @Autowired
    private ApplicationEventPublisher publisher;

    // 使用 @SpyBean 对 EventListener 进行监控，方便验证方法是否被调用
    @SpyBean
    private EventListener eventListener;

    @Test
    public void testHandleAccountCreated() {
        AccountDTO account = new AccountDTO();
        AccountCreatedEvent event = new AccountCreatedEvent(account);
        publisher.publishEvent(event);
        // 验证 handleAccountCreated 方法被调用
        verify(eventListener, timeout(1000)).handleAccountCreated(event);
    }

    @Test
    public void testHandleAccountStatusChanged() {
        AccountDTO account = new AccountDTO();
        AccountStatusChangedEvent event = new AccountStatusChangedEvent(account);
        publisher.publishEvent(event);
        // 此事件会触发 handleAccountStatusChanged 方法
        verify(eventListener, timeout(1000)).handleAccountStatusChanged(event);
        // 同时因为 handleCardCreated 也订阅了 AccountStatusChangedEvent，所以也会被调用
        verify(eventListener, timeout(1000)).handleCardCreated(event);
    }

    @Test
    public void testHandleCardStatusChanged() {
        CardDTO card = new CardDTO();
        CardStatusChangedEvent event = new CardStatusChangedEvent(card);
        publisher.publishEvent(event);
        verify(eventListener, timeout(1000)).handleCardStatusChanged(event);
    }

    @Test
    public void testHandleCardAssigned() {
        CardDTO card = new CardDTO();
        CardAssignedEvent event = new CardAssignedEvent(card);
        publisher.publishEvent(event);
        verify(eventListener, timeout(1000)).handleCardAssigned(event);
    }
}

