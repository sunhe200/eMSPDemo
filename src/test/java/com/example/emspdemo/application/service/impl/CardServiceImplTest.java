package com.example.emspdemo.application.service.impl;

import com.example.emspdemo.application.command.AssignCardCommand;
import com.example.emspdemo.application.command.ChangeCardStatusCommand;
import com.example.emspdemo.application.command.CreateCardCommand;
import com.example.emspdemo.domain.Account;
import com.example.emspdemo.domain.Card;
import com.example.emspdemo.domain.enums.CardStatus;
import com.example.emspdemo.domain.event.DomainEvent;
import com.example.emspdemo.repository.dao.AccountDao;
import com.example.emspdemo.repository.dao.CardDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CardServiceImplTest {

    @Mock
    private CardDao cardDao;

    @Mock
    private AccountDao accountDao;

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private CardServiceImpl cardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCard_Success() {
        // 准备 CreateCardCommand
        CreateCardCommand command = new CreateCardCommand();
        command.setEditor("admin");
        // 模拟 cardDao.save: 新增时将 Card 的 id 设为 1L
        doAnswer(invocation -> {
            Card card = invocation.getArgument(0);
            card.setId(1L);
            return null;
        }).when(cardDao).save(any(Card.class));

        // 调用创建方法
        Card card = cardService.createCard(command);
        assertNotNull(card);
        assertEquals(1L, card.getId());
        // 由于 Card.create 内部会记录领域事件，所以至少发布一次领域事件
        verify(publisher, atLeastOnce()).publishEvent(any(DomainEvent.class));
        // 验证 cardDao.save 调用一次
        verify(cardDao, times(1)).save(any(Card.class));
    }

    @Test
    void testAssignCardToAccount_Success() {
        Long cardId = 1L;
        Long accountId = 100L;
        // 构造 AssignCardCommand
        AssignCardCommand command = new AssignCardCommand();
        command.setAccountId(accountId);
        command.setEditor("operator");

        // 模拟已存在的 Card 与 Account
        Card card = new Card();
        card.setId(cardId);
        when(cardDao.findById(cardId)).thenReturn(card);
        Account account = new Account();
        account.setId(accountId);
        when(accountDao.findById(accountId)).thenReturn(account);
        doNothing().when(cardDao).save(any(Card.class));

        Card updatedCard = cardService.assignCardToAccount(cardId, command);
        assertNotNull(updatedCard);
        // 检查分配后的 accountId 是否设置正确
        assertEquals(accountId, updatedCard.getAccountId());
        verify(cardDao, times(1)).findById(cardId);
        verify(accountDao, times(1)).findById(accountId);
        verify(cardDao, times(1)).save(any(Card.class));
        verify(publisher, atLeastOnce()).publishEvent(any(DomainEvent.class));
    }

    @Test
    void testAssignCardToAccount_CardNotFound() {
        Long cardId = 1L;
        Long accountId = 100L;
        AssignCardCommand command = new AssignCardCommand();
        command.setAccountId(accountId);
        command.setEditor("operator");

        when(cardDao.findById(cardId)).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> cardService.assignCardToAccount(cardId, command));
        assertEquals("Card or Account not found", ex.getMessage());
    }

    @Test
    void testAssignCardToAccount_AccountNotFound() {
        Long cardId = 1L;
        Long accountId = 100L;
        AssignCardCommand command = new AssignCardCommand();
        command.setAccountId(accountId);
        command.setEditor("operator");

        Card card = new Card();
        card.setId(cardId);
        when(cardDao.findById(cardId)).thenReturn(card);
        when(accountDao.findById(accountId)).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> cardService.assignCardToAccount(cardId, command));
        assertEquals("Card or Account not found", ex.getMessage());
    }

    @Test
    void testChangeCardStatus_Success() {
        Long cardId = 1L;
        ChangeCardStatusCommand command = new ChangeCardStatusCommand();
        command.setNewStatus(CardStatus.ACTIVATED);
        command.setEditor("operator");

        Card card = new Card();
        card.setId(cardId);
        when(cardDao.findById(cardId)).thenReturn(card);
        doNothing().when(cardDao).save(any(Card.class));

        Card updatedCard = cardService.changeCardStatus(cardId, command);
        assertNotNull(updatedCard);
        assertEquals(CardStatus.ACTIVATED, updatedCard.getStatus());
        verify(cardDao, times(1)).findById(cardId);
        verify(cardDao, times(1)).save(any(Card.class));
        verify(publisher, atLeastOnce()).publishEvent(any(DomainEvent.class));
    }

    @Test
    void testChangeCardStatus_CardNotFound() {
        Long cardId = 1L;
        ChangeCardStatusCommand command = new ChangeCardStatusCommand();
        command.setNewStatus(CardStatus.ACTIVATED);
        command.setEditor("operator");

        when(cardDao.findById(cardId)).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> cardService.changeCardStatus(cardId, command));
        assertEquals("Card not found", ex.getMessage());
    }
}
