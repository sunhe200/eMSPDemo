package com.example.emspdemo.domain.event;

import com.example.emspdemo.domain.Card;
import com.example.emspdemo.domain.enums.AccountStatus;
import com.example.emspdemo.domain.enums.CardStatus;
import com.example.emspdemo.presentation.dto.CardDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Getter
@AllArgsConstructor
public class CardStatusChangedEvent implements DomainEvent, Serializable {
    @Serial
    private static final long serialVersionUID = -2278595523276191259L;
    private final Card card;
    private String eventId;
    private Date eventTime;
    private CardStatus newStatus;
}
