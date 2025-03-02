package com.example.emspdemo.repository.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.emspdemo.domain.Card;
import com.example.emspdemo.domain.enums.CardStatus;
import com.example.emspdemo.domain.enums.TokenProp;
import com.example.emspdemo.domain.enums.TokenType;
import com.example.emspdemo.domain.vo.Token;
import com.example.emspdemo.repository.mapper.CardMapper;
import com.example.emspdemo.repository.mapper.TokenMapper;
import com.example.emspdemo.repository.po.CardPO;
import com.example.emspdemo.repository.po.TokenPO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CardDaoImplTest {

    @Mock
    private CardMapper cardMapper;

    @Mock
    private TokenMapper tokenMapper;

    @InjectMocks
    private CardDaoImpl cardDaoImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave_InsertNewCard() {
        // 构造一个新的 Card（id 为 null），并添加两个 token（用于 uid 和 visibleNumber）
        Card card = new Card();
        card.setEditor("admin");
        card.setStatus(CardStatus.CREATED);
        card.setLastUpdated(new Date());
        Token tokenUid = new Token(TokenType.EMAID, TokenProp.UID, "UID123");
        Token tokenVisible = new Token(TokenType.EMAID, TokenProp.VISIBLE_NUMBER, "VN456");
        card.setTokens(Arrays.asList(tokenUid, tokenVisible));

        // 模拟 cardMapper.insert：当调用 insert 时，模拟生成 id = 1L
        doAnswer(invocation -> {
            CardPO po = invocation.getArgument(0);
            po.setId(1L);
            return 1;
        }).when(cardMapper).insert(any(CardPO.class));

        // 模拟 tokenMapper.insert 返回 1
        when(tokenMapper.insert(any(TokenPO.class))).thenReturn(1);

        // 调用 save 方法
        cardDaoImpl.save(card);

        // 插入路径：保存后 Card 的 id 应为 1
        assertEquals(1L, card.getId());
        verify(cardMapper, times(1)).insert(any(CardPO.class));
        // tokenMapper.insert 应按 token 数量调用
        verify(tokenMapper, times(card.getTokens().size())).insert(any(TokenPO.class));
    }

    @Test
    void testSave_UpdateExistingCard() {
        // 构造一个已有 id 的 Card（更新路径），tokens 部分不再处理
        Card card = new Card();
        card.setId(2L);
        card.setEditor("admin");
        card.setStatus(CardStatus.CREATED);
        card.setLastUpdated(new Date());
        card.setTokens(Collections.emptyList());

        // 调用 save 方法（更新路径）
        cardDaoImpl.save(card);

        // 更新路径：调用 updateById 而不是 insert，且不调用 tokenMapper.insert
        verify(cardMapper, times(1)).updateById(any(CardPO.class));
        verify(tokenMapper, never()).insert(any(TokenPO.class));
    }

    @Test
    void testFindById_NotFound() {
        // 模拟当 id 为 1 时，不存在对应的 CardPO
        when(cardMapper.selectById(1L)).thenReturn(null);
        Card card = cardDaoImpl.findById(1L);
        assertNull(card);
    }

    @Test
    void testFindById_Found() {
        // 构造 AccountPO 模拟数据
        CardPO cardPO = new CardPO();
        cardPO.setId(1L);
        cardPO.setEditor("admin");
        cardPO.setStatus(CardStatus.CREATED);
        Date now = new Date();
        cardPO.setLastUpdated(now);
        cardPO.setAccountId(100L);

        when(cardMapper.selectById(1L)).thenReturn(cardPO);

        // 构造 TokenPO 列表，包含两个 token（分别表示 uid 与 visibleNumber）
        TokenPO tokenPO1 = new TokenPO();
        tokenPO1.setExternalId(1L);
        tokenPO1.setTokenType(TokenType.EMAID);
        tokenPO1.setPropName(TokenProp.UID);
        tokenPO1.setPropValue("UID123");

        TokenPO tokenPO2 = new TokenPO();
        tokenPO2.setExternalId(1L);
        tokenPO2.setTokenType(TokenType.EMAID);
        tokenPO2.setPropName(TokenProp.VISIBLE_NUMBER);
        tokenPO2.setPropValue("VN456");

        List<TokenPO> tokenPOs = Arrays.asList(tokenPO1, tokenPO2);
        when(tokenMapper.selectList(any(QueryWrapper.class))).thenReturn(tokenPOs);

        // 调用 findById 方法
        Card card = cardDaoImpl.findById(1L);
        assertNotNull(card);
        assertEquals(1L, card.getId());
        assertEquals("admin", card.getEditor());
        assertEquals(100L, card.getAccountId());
        // 验证 tokens 是否正确装配
        List<Token> tokens = card.getTokens();
        assertNotNull(tokens);
        assertEquals(2, tokens.size());

        // 验证 uid token
        Token uidToken = tokens.stream()
                .filter(t -> t.getPropName() == TokenProp.UID)
                .findFirst()
                .orElse(null);
        assertNotNull(uidToken);
        assertEquals("UID123", uidToken.getPropValue());

        // 验证 visibleNumber token
        Token visibleToken = tokens.stream()
                .filter(t -> t.getPropName() == TokenProp.VISIBLE_NUMBER)
                .findFirst()
                .orElse(null);
        assertNotNull(visibleToken);
        assertEquals("VN456", visibleToken.getPropValue());
    }
}
