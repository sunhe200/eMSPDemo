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
import com.example.emspdemo.util.RFIDUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CardServiceImplTest {

    @Mock
    private CardMapper cardMapper;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private CardServiceImpl cardService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateCard() {
        // 给定输入 CardDTO
        CardDTO cardDTO = new CardDTO();

        // 准备一个模拟的 CardPO 作为 cardMapper.selectOne 返回结果
        CardPO cardPO = new CardPO();
        cardPO.setId(1L);
        cardPO.setCardNumber("123456");
        cardPO.setStatus(CardStatus.CREATED);
        cardPO.setUid("uid-123");
        cardPO.setLastUpdated(new Date());

        // 当调用 selectOne 时返回准备的 cardPO
        when(cardMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(cardPO);

        // 调用 createCard 方法
        cardService.createCard(cardDTO);

        // 验证 insert 与 selectOne 方法各调用一次
        verify(cardMapper, times(1)).insert(any(CardPO.class));
        verify(cardMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));

        // 捕获并验证发布的 CardCreatedEvent
        ArgumentCaptor<CardCreatedEvent> eventCaptor = ArgumentCaptor.forClass(CardCreatedEvent.class);
        verify(publisher, times(1)).publishEvent(eventCaptor.capture());
        CardCreatedEvent publishedEvent = eventCaptor.getValue();
        assertNotNull(publishedEvent.getCard(), "CardCreatedEvent 中的 CardDTO 不能为空");
        // 验证状态为 CREATED
        assertEquals(CardStatus.CREATED, publishedEvent.getCard().getStatus(), "卡片状态应为 CREATED");
        // 验证卡号非空
        assertNotNull(publishedEvent.getCard().getCardNumber(), "卡号应被设置");
    }

    @Test
    public void testAssignCardToAccount() {
        Long cardId = 1L;
        Long accountId = 2L;
        String editor = "testEditor";

        // 准备模拟的 CardPO 与 AccountPO
        CardPO cardPO = new CardPO();
        cardPO.setId(cardId);
        AccountPO accountPO = new AccountPO();
        accountPO.setId(accountId);

        // 模拟 cardMapper.selectById 在方法中被调用两次：第一次获取卡片，第二次获取更新后的卡片
        when(cardMapper.selectById(cardId)).thenReturn(cardPO, cardPO);
        when(accountMapper.selectById(accountId)).thenReturn(accountPO);

        // 调用 assignCardToAccount 方法
        cardService.assignCardToAccount(cardId, accountId, editor);

        // 验证 updateById 调用一次
        verify(cardMapper, times(1)).updateById(cardPO);

        // 捕获并验证发布的 CardAssignedEvent
        ArgumentCaptor<CardAssignedEvent> eventCaptor = ArgumentCaptor.forClass(CardAssignedEvent.class);
        verify(publisher, times(1)).publishEvent(eventCaptor.capture());
        CardAssignedEvent publishedEvent = eventCaptor.getValue();
        assertNotNull(publishedEvent.getCard(), "CardAssignedEvent 中的 CardDTO 不能为空");
        // 验证卡片已关联 accountId
        assertEquals(accountId, publishedEvent.getCard().getAccountId(), "卡片的 accountId 应被更新");
    }

    @Test
    public void testAssignCardToAccount_CardNotFound() {
        Long cardId = 1L;
        Long accountId = 2L;
        String editor = "testEditor";

        // 模拟 cardMapper.selectById 返回 null，表示卡片不存在
        when(cardMapper.selectById(cardId)).thenReturn(null);
        // account 存在
        AccountPO accountPO = new AccountPO();
        accountPO.setId(accountId);
        when(accountMapper.selectById(accountId)).thenReturn(accountPO);

        // 断言抛出异常
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> cardService.assignCardToAccount(cardId, accountId, editor));
        assertEquals("Card or Account not found", exception.getMessage());
    }

    @Test
    public void testAssignCardToAccount_AccountNotFound() {
        Long cardId = 1L;
        Long accountId = 2L;
        String editor = "testEditor";

        // 模拟卡片存在，但账户不存在
        CardPO cardPO = new CardPO();
        cardPO.setId(cardId);
        when(cardMapper.selectById(cardId)).thenReturn(cardPO);
        when(accountMapper.selectById(accountId)).thenReturn(null);

        // 断言抛出异常
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> cardService.assignCardToAccount(cardId, accountId, editor));
        assertEquals("Card or Account not found", exception.getMessage());
    }

    @Test
    public void testChangeCardStatus() {
        Long cardId = 1L;
        CardStatus newStatus = CardStatus.ACTIVATED; // 假设 ACTIVATED 为有效枚举值
        String editor = "testEditor";

        // 准备模拟的 CardPO
        CardPO cardPO = new CardPO();
        cardPO.setId(cardId);
        cardPO.setStatus(CardStatus.CREATED);

        // 模拟 selectById 两次调用，分别获取更新前后对象
        when(cardMapper.selectById(cardId)).thenReturn(cardPO, cardPO);

        // 调用 changeCardStatus 方法
        cardService.changeCardStatus(cardId, newStatus, editor);

        // 验证 updateById 调用一次
        verify(cardMapper, times(1)).updateById(cardPO);

        // 捕获并验证发布的 CardStatusChangedEvent
        ArgumentCaptor<CardStatusChangedEvent> eventCaptor = ArgumentCaptor.forClass(CardStatusChangedEvent.class);
        verify(publisher, times(1)).publishEvent(eventCaptor.capture());
        CardStatusChangedEvent publishedEvent = eventCaptor.getValue();
        assertNotNull(publishedEvent.getCard(), "CardStatusChangedEvent 中的 CardDTO 不能为空");
        // 验证卡片状态更新为 newStatus
        assertEquals(newStatus, publishedEvent.getCard().getStatus(), "卡片状态应更新为 ACTIVATED");
    }

    @Test
    public void testChangeCardStatus_CardNotFound() {
        Long cardId = 1L;
        CardStatus newStatus = CardStatus.ACTIVATED;
        String editor = "testEditor";

        // 模拟 cardMapper.selectById 返回 null，表示卡片不存在
        when(cardMapper.selectById(cardId)).thenReturn(null);

        // 断言抛出异常
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> cardService.changeCardStatus(cardId, newStatus, editor));
        assertEquals("Card not found", exception.getMessage());
    }
}
