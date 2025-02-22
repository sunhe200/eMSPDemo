package com.example.emspdemo.domain.event;

import com.example.emspdemo.domain.dto.AccountDTO;
import lombok.Getter;

@Getter
public class AccountCreatedEvent {
    private final AccountDTO account;

    public AccountCreatedEvent(AccountDTO account) {
        this.account = account;
    }
}
