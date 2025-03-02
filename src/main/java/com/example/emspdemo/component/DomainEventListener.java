package com.example.emspdemo.component;

import com.example.emspdemo.domain.event.AccountCreatedEvent;
import com.example.emspdemo.domain.event.AccountStatusChangedEvent;
import com.example.emspdemo.domain.event.CardAssignedEvent;
import com.example.emspdemo.domain.event.CardStatusChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DomainEventListener {

    @EventListener
    public void handleAccountCreated(AccountCreatedEvent event) {
        // Todo 发送消息到中间件
        log.info("Account created: {}", event);
    }

    @EventListener
    public void handleAccountStatusChanged(AccountStatusChangedEvent event) {
        // Todo 发送消息到中间件
        log.info("Account status changed: {}", event);
    }

    @EventListener
    public void handleCardCreated(AccountStatusChangedEvent event) {
        // Todo 发送消息到中间件
        log.info("Card created: {}", event);
    }

    @EventListener
    public void handleCardStatusChanged(CardStatusChangedEvent event) {
        // Todo 发送消息到中间件
        log.info("Card status changed: {}", event);
    }

    @EventListener
    public void handleCardAssigned(CardAssignedEvent event) {
        // Todo 发送消息到中间件
        log.info("Card assigned: {}", event);
    }
}
