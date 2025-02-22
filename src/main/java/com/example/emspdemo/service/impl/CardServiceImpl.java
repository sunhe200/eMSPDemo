package com.example.emspdemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.emspdemo.domain.convert.CardConvert;
import com.example.emspdemo.domain.dto.CardDTO;
import com.example.emspdemo.domain.enums.CardStatus;
import com.example.emspdemo.domain.event.CardAssignedEvent;
import com.example.emspdemo.domain.event.CardCreatedEvent;
import com.example.emspdemo.domain.event.CardStatusChangedEvent;
import com.example.emspdemo.domain.po.AccountPO;
import com.example.emspdemo.domain.po.CardPO;
import com.example.emspdemo.repository.AccountMapper;
import com.example.emspdemo.repository.CardMapper;
import com.example.emspdemo.service.CardService;
import com.example.emspdemo.util.RFIDUtil;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CardServiceImpl implements CardService {
    private final CardMapper cardMapper;
    private final AccountMapper accountMapper;
    private final ApplicationEventPublisher publisher;

    public CardServiceImpl(CardMapper cardMapper, AccountMapper accountMapper, ApplicationEventPublisher publisher) {
        this.cardMapper = cardMapper;
        this.accountMapper = accountMapper;
        this.publisher = publisher;
    }

    @Override
    public void createCard(CardDTO cardDTO) {
        CardPO cardPO = CardConvert.INSTANCE.dto2PO(cardDTO);
        String cardNumber = RFIDUtil.generateUniqueVisibleNumber();
        cardPO.setCardNumber(cardNumber);
        cardPO.setStatus(CardStatus.CREATED);
        String uid = RFIDUtil.generateUniqueUID();
        cardPO.setUid(uid);
        cardPO.setLastUpdated(new Date());
        cardMapper.insert(cardPO);
        LambdaQueryWrapper<CardPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CardPO::getUid, uid);
        publisher.publishEvent(
                new CardCreatedEvent(CardConvert.INSTANCE.po2Dto(cardMapper.selectOne(wrapper))));
    }

    @Override
    public void assignCardToAccount(Long cardId, Long accountId, String editor) {
        CardPO card = cardMapper.selectById(cardId);
        AccountPO account = accountMapper.selectById(accountId);
        if (card == null || account == null) {
            throw new RuntimeException("Card or Account not found");
        }
        card.setAccountId(accountId);
        card.setLastUpdated(new Date());
        cardMapper.updateById(card);
        publisher.publishEvent(
                new CardAssignedEvent(CardConvert.INSTANCE.po2Dto(
                        cardMapper.selectById(cardId))));
    }

    @Override
    public void changeCardStatus(Long cardId, CardStatus status, String editor) {
        CardPO card = cardMapper.selectById(cardId);
        if (card == null) {
            throw new RuntimeException("Card not found");
        }
        card.setStatus(status);
        card.setLastUpdated(new Date());
        cardMapper.updateById(card);
        publisher.publishEvent(
                new CardStatusChangedEvent(CardConvert.INSTANCE.po2Dto(
                        cardMapper.selectById(cardId))));
    }
}
