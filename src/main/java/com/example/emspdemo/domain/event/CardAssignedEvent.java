package com.example.emspdemo.domain.event;

import com.example.emspdemo.domain.dto.CardDTO;
import lombok.Getter;

@Getter
public class CardAssignedEvent {
    private final CardDTO card;

    public CardAssignedEvent(CardDTO card) {
        this.card = card;
    }
}
