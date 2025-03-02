package com.example.emspdemo.domain.event;

import com.example.emspdemo.domain.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Getter
@AllArgsConstructor
public class AccountCreatedEvent implements DomainEvent, Serializable {
    @Serial
    private static final long serialVersionUID = 3635855030417351061L;
    private final Account account;
    private String eventId;
    private Date eventTime;
}
