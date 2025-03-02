package com.example.emspdemo.assembler;

import com.example.emspdemo.domain.Card;
import com.example.emspdemo.domain.enums.CardStatus;
import com.example.emspdemo.domain.enums.TokenProp;
import com.example.emspdemo.domain.enums.TokenType;
import com.example.emspdemo.domain.vo.Token;
import com.example.emspdemo.presentation.dto.CardDTO;
import com.example.emspdemo.repository.po.CardPO;
import com.example.emspdemo.repository.po.TokenPO;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CardAssemblerTest {

    @Test
    void testToDTO() {
        // 构造领域对象 Card
        Card card = new Card();
        card.setId(1L);
        card.setAccountId(100L);
        card.setStatus(CardStatus.ACTIVATED);
        Date now = new Date();
        card.setLastUpdated(now);
        card.setEditor("testEditor");

        // 构造 token 列表，其中包含 UID 和 visibleNumber 两个 token
        Token tokenUid = new Token(TokenType.RFID, TokenProp.UID, "UID123");
        Token tokenVisible = new Token(TokenType.RFID, TokenProp.VISIBLE_NUMBER, "VN456");
        card.setTokens(Arrays.asList(tokenUid, tokenVisible));

        // 调用转换方法
        CardDTO dto = CardAssembler.toDTO(card);

        // 验证转换结果
        assertNotNull(dto);
        assertEquals(card.getId(), dto.getId());
        assertEquals(card.getAccountId(), dto.getAccountId());
        assertEquals(card.getStatus(), dto.getStatus());
        assertEquals(card.getLastUpdated(), dto.getLastUpdated());
        assertEquals(card.getEditor(), dto.getEditor());
        // 验证从 token 列表中映射的 uid 与 cardNumber
        assertEquals("UID123", dto.getUid());
        assertEquals("VN456", dto.getCardNumber());
    }

    @Test
    void testToDTO_Null() {
        // 如果传入 null，应返回 null
        assertNull(CardAssembler.toDTO(null));
    }

    @Test
    void testToDomain() {
        // 构造持久化对象 CardPO
        CardPO po = new CardPO();
        po.setId(2L);
        po.setAccountId(200L);
        po.setStatus(CardStatus.DEACTIVATED);
        Date now = new Date();
        po.setLastUpdated(now);
        po.setEditor("domainEditor");

        // 构造 TokenPO 列表，其中一个记录用于 UID，一个用于 visibleNumber
        TokenPO tokenPO1 = new TokenPO();
        tokenPO1.setTokenType(TokenType.RFID);
        tokenPO1.setPropName(TokenProp.UID);
        tokenPO1.setPropValue("UID789");

        TokenPO tokenPO2 = new TokenPO();
        tokenPO2.setTokenType(TokenType.RFID);
        tokenPO2.setPropName(TokenProp.VISIBLE_NUMBER);
        tokenPO2.setPropValue("VN101112");

        List<TokenPO> tokenPOs = Arrays.asList(tokenPO1, tokenPO2);

        // 调用转换方法，将 CardPO 与 tokenPO 列表转换为领域对象 Card
        Card card = CardAssembler.toDomain(po, tokenPOs);

        // 验证结果
        assertNotNull(card);
        assertEquals(po.getId(), card.getId());
        assertEquals(po.getAccountId(), card.getAccountId());
        assertEquals(po.getStatus(), card.getStatus());
        assertEquals(po.getLastUpdated(), card.getLastUpdated());
        assertEquals(po.getEditor(), card.getEditor());

        // 验证 Token 列表是否正确装配
        List<Token> tokens = card.getTokens();
        assertNotNull(tokens);
        assertEquals(2, tokens.size());

        Token uidToken = tokens.stream()
                .filter(t -> t.getPropName() == TokenProp.UID)
                .findFirst().orElse(null);
        Token visibleToken = tokens.stream()
                .filter(t -> t.getPropName() == TokenProp.VISIBLE_NUMBER)
                .findFirst().orElse(null);
        assertNotNull(uidToken);
        assertEquals("UID789", uidToken.getPropValue());
        assertNotNull(visibleToken);
        assertEquals("VN101112", visibleToken.getPropValue());
    }

    @Test
    void testToDomain_Null() {
        // 如果 AccountPO 为 null，则返回 null
        assertNull(CardAssembler.toDomain(null, null));
    }

    @Test
    void testToPO() {
        // 构造领域对象 Card
        Card card = new Card();
        card.setId(3L);
        card.setAccountId(300L);
        card.setStatus(CardStatus.ACTIVATED);
        Date now = new Date();
        card.setLastUpdated(now);
        card.setEditor("poEditor");
        // tokens 部分不参与 PO 转换，此处可设置任意值
        card.setTokens(List.of(new Token(TokenType.RFID, TokenProp.UID, "UID333")));

        // 调用转换方法
        CardPO po = CardAssembler.toPO(card);

        // 验证结果
        assertNotNull(po);
        assertEquals(card.getId(), po.getId());
        assertEquals(card.getAccountId(), po.getAccountId());
        assertEquals(card.getStatus(), po.getStatus());
        assertEquals(card.getLastUpdated(), po.getLastUpdated());
        assertEquals(card.getEditor(), po.getEditor());
    }

    @Test
    void testToPO_Null() {
        // 如果传入 null，应返回 null
        assertNull(CardAssembler.toPO(null));
    }
}
