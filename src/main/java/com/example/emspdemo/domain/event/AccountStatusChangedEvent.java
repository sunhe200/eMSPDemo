package com.example.emspdemo.domain.event;

import com.example.emspdemo.domain.dto.AccountDTO;
import lombok.Getter;

@Getter
public class AccountStatusChangedEvent {
    private final AccountDTO account;

    public AccountStatusChangedEvent(AccountDTO account) {
        this.account = account;
    }
}
