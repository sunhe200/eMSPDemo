package com.example.emspdemo.domain.event;

import com.example.emspdemo.domain.Account;
import com.example.emspdemo.domain.enums.AccountStatus;
import com.example.emspdemo.presentation.dto.AccountDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Getter
@AllArgsConstructor
public class AccountStatusChangedEvent implements DomainEvent, Serializable {
    @Serial
    private static final long serialVersionUID = 4452368468265651785L;
    private final Account account;
    private String eventId;
    private Date eventTime;
    private AccountStatus newStatus;
}
