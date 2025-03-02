package com.example.emspdemo.assembler;

import com.example.emspdemo.domain.enums.TokenType;
import com.example.emspdemo.domain.vo.Token;
import com.example.emspdemo.domain.enums.TokenProp;
import com.example.emspdemo.repository.po.TokenPO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenAssemblerTest {

    @Test
    void testToPO_WithValidToken() {
        Token token = new Token(TokenType.RFID, TokenProp.UID, "123456");
        Long externalId = 100L;
        TokenPO tokenPO = TokenAssembler.toPO(token, externalId);

        assertNotNull(tokenPO, "转换后的 TokenPO 不应为 null");
        assertEquals(externalId, tokenPO.getExternalId(), "externalId 不匹配");
        assertEquals(TokenType.RFID, tokenPO.getTokenType(), "tokenType 不匹配");
        assertEquals(TokenProp.UID, tokenPO.getPropName(), "propName 不匹配");
        assertEquals("123456", tokenPO.getPropValue(), "propValue 不匹配");
    }

    @Test
    void testToPO_WithNullToken() {

        TokenPO tokenPO = TokenAssembler.toPO(null, 123L);
        assertNull(tokenPO, "传入 null 时，转换结果应为 null");
    }
}
