package com.example.emspdemo.domain.event;

import com.example.emspdemo.domain.dto.CardDTO;
import lombok.Getter;

@Getter
public class CardStatusChangedEvent {
    private final CardDTO card;

    public CardStatusChangedEvent(CardDTO card) {
        this.card = card;
    }
}
