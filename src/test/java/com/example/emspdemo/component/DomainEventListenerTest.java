package com.example.emspdemo.component;

import cn.hutool.core.util.IdUtil;
import com.example.emspdemo.domain.Account;
import com.example.emspdemo.domain.Card;
import com.example.emspdemo.domain.enums.AccountStatus;
import com.example.emspdemo.domain.enums.CardStatus;
import com.example.emspdemo.domain.event.AccountCreatedEvent;
import com.example.emspdemo.domain.event.AccountStatusChangedEvent;
import com.example.emspdemo.domain.event.CardAssignedEvent;
import com.example.emspdemo.domain.event.CardStatusChangedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class DomainEventListenerTest {

    @Autowired
    private ApplicationEventPublisher publisher;

    @Test
    void testHandleAccountCreatedEvent() {

        AccountCreatedEvent event = new AccountCreatedEvent(new Account(), IdUtil.fastSimpleUUID(), new Date());
        assertDoesNotThrow(() -> publisher.publishEvent(event));
    }

    @Test
    void testHandleAccountStatusChangedEvent() {

        AccountStatusChangedEvent event = new AccountStatusChangedEvent(new Account(), IdUtil.fastSimpleUUID(), new Date(), AccountStatus.ACTIVATED);
        assertDoesNotThrow(() -> publisher.publishEvent(event));
    }

    @Test
    void testHandleCardCreatedEvent() {

        AccountStatusChangedEvent event = new AccountStatusChangedEvent(new Account(),  IdUtil.fastSimpleUUID(), new Date(), AccountStatus.CREATED);
        assertDoesNotThrow(() -> publisher.publishEvent(event));
    }

    @Test
    void testHandleCardStatusChangedEvent() {

        CardStatusChangedEvent event = new CardStatusChangedEvent(new Card(),  IdUtil.fastSimpleUUID(), new Date(), CardStatus.ACTIVATED);
        assertDoesNotThrow(() -> publisher.publishEvent(event));
    }

    @Test
    void testHandleCardAssignedEvent() {
        CardAssignedEvent event = new CardAssignedEvent(new Card(), IdUtil.fastSimpleUUID(), new Date(),123L);
        assertDoesNotThrow(() -> publisher.publishEvent(event));
    }
}
