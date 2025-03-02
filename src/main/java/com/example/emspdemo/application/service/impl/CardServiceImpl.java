package com.example.emspdemo.application.service.impl;

import com.example.emspdemo.application.command.AssignCardCommand;
import com.example.emspdemo.application.command.ChangeCardStatusCommand;
import com.example.emspdemo.application.command.CreateCardCommand;
import com.example.emspdemo.domain.Account;
import com.example.emspdemo.domain.Card;
import com.example.emspdemo.domain.event.DomainEvent;
import com.example.emspdemo.repository.dao.AccountDao;
import com.example.emspdemo.repository.dao.CardDao;
import com.example.emspdemo.application.service.CardService;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardServiceImpl implements CardService {
    private final CardDao cardDao;
    private final AccountDao accountDao;
    private final ApplicationEventPublisher publisher;

    public CardServiceImpl(CardDao cardDao, AccountDao accountDao, ApplicationEventPublisher publisher) {
        this.cardDao = cardDao;
        this.accountDao = accountDao;
        this.publisher = publisher;
    }


    @Override
    @Transactional
    public Card createCard(CreateCardCommand command) {
        Card card = Card.create(command.getEditor());
        cardDao.save(card);
        publishDomainEvents(card);
        return card;
    }

    @Override
    @Transactional
    public Card assignCardToAccount(Long cardId, AssignCardCommand command) {
        Card card = cardDao.findById(cardId);
        Account account = accountDao.findById(command.getAccountId());
        if (card == null || account == null) {
            throw new RuntimeException("Card or Account not found");
        }
        card.assignToAccount(command.getAccountId(), command.getEditor());
        cardDao.save(card);
        publishDomainEvents(card);
        return card;
    }

    @Override
    @Transactional
    public Card changeCardStatus(Long cardId, ChangeCardStatusCommand command) {
        Card card = cardDao.findById(cardId);
        if (card == null) {
            throw new RuntimeException("Card not found");
        }
        card.changeStatus(command.getNewStatus(), command.getEditor());
        cardDao.save(card);
        publishDomainEvents(card);
        return card;
    }

    private void publishDomainEvents(Card card) {
        List<DomainEvent> events = card.getDomainEvents();
        if (events != null) {
            for (DomainEvent event : events) {
                publisher.publishEvent(event);
            }
        }
        card.clearDomainEvents();
    }
}
