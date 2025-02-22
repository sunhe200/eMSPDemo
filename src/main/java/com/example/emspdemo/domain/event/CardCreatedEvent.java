package com.example.emspdemo.domain.event;

import com.example.emspdemo.domain.dto.CardDTO;
import lombok.Getter;

@Getter
public class CardCreatedEvent {
    private final CardDTO card;

    public CardCreatedEvent(CardDTO card) {
        this.card = card;
    }
}
