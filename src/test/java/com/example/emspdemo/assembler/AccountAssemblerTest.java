package com.example.emspdemo.assembler;

import com.example.emspdemo.domain.Account;
import com.example.emspdemo.domain.enums.AccountStatus;
import com.example.emspdemo.domain.enums.TokenProp;
import com.example.emspdemo.domain.enums.TokenType;
import com.example.emspdemo.domain.vo.Token;
import com.example.emspdemo.presentation.dto.AccountDTO;
import com.example.emspdemo.repository.po.AccountPO;
import com.example.emspdemo.repository.po.TokenPO;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountAssemblerTest {

    @Test
    void testToDTO_withValidAccount() {
        // 构造领域对象 Account
        Account account = new Account();
        account.setId(1L);
        account.setEmail("test@example.com");
        account.setStatus(AccountStatus.ACTIVATED);
        Date now = new Date();
        account.setLastUpdated(now);
        account.setEditor("admin");
        // 构造 Token 列表，其中包含一个合同编号 token
        Token token1 = new Token(TokenType.EMAID, TokenProp.CONTRACT_ID, "CONTRACT-123");
        Token token2 = new Token(TokenType.RFID, TokenProp.UID, "UID-456");
        account.setTokens(Arrays.asList(token1, token2));

        // 调用转换方法
        AccountDTO dto = AccountAssembler.toDTO(account);

        // 验证转换结果
        assertNotNull(dto);
        assertEquals(account.getId(), dto.getId());
        assertEquals(account.getEmail(), dto.getEmail());
        assertEquals(account.getStatus(), dto.getStatus());
        assertEquals(account.getLastUpdated(), dto.getLastUpdated());
        assertEquals(account.getEditor(), dto.getEditor());
        // 检查从 token 列表中提取的合同编号
        assertEquals("CONTRACT-123", dto.getContractId());
    }

    @Test
    void testToDTO_withNullAccount() {
        // 如果输入 null，返回也应为 null
        assertNull(AccountAssembler.toDTO(null));
    }

    @Test
    void testToDomain_withValidPOs() {
        // 构造持久化对象 AccountPO
        AccountPO po = new AccountPO();
        po.setId(2L);
        po.setEmail("domain@example.com");
        po.setStatus(AccountStatus.DEACTIVATED);
        Date now = new Date();
        po.setLastUpdated(now);
        po.setEditor("editor1");

        // 构造 TokenPO 列表，其中一个记录为合同编号
        TokenPO tokenPO1 = new TokenPO();
        tokenPO1.setTokenType(TokenType.EMAID);
        tokenPO1.setPropName(TokenProp.CONTRACT_ID);
        tokenPO1.setPropValue("CONTRACT-456");

        TokenPO tokenPO2 = new TokenPO();
        tokenPO2.setTokenType(TokenType.RFID);
        tokenPO2.setPropName(TokenProp.UID);
        tokenPO2.setPropValue("UID-789");

        List<TokenPO> tokenPOs = Arrays.asList(tokenPO1, tokenPO2);

        // 调用转换方法：将 AccountPO 与 tokenPO 列表转换为领域对象 Account
        Account account = AccountAssembler.toDomain(po, tokenPOs);

        // 验证结果
        assertNotNull(account);
        assertEquals(po.getId(), account.getId());
        assertEquals(po.getEmail(), account.getEmail());
        assertEquals(po.getStatus(), account.getStatus());
        assertEquals(po.getLastUpdated(), account.getLastUpdated());
        assertEquals(po.getEditor(), account.getEditor());
        // 检查 Token 列表
        List<Token> tokens = account.getTokens();
        assertNotNull(tokens);
        assertEquals(2, tokens.size());
        // 验证合同编号 token的值
        Token contractToken = tokens.stream()
                .filter(token -> token.getPropName() == TokenProp.CONTRACT_ID)
                .findFirst().orElse(null);
        assertNotNull(contractToken);
        assertEquals("CONTRACT-456", contractToken.getPropValue());
    }

    @Test
    void testToDomain_withNullPO() {
        // 如果 AccountPO 为 null，则返回 null
        assertNull(AccountAssembler.toDomain(null, null));
    }

    @Test
    void testToPO_withValidAccount() {
        // 构造领域对象 Account
        Account account = new Account();
        account.setId(3L);
        account.setEmail("po@example.com");
        account.setStatus(AccountStatus.ACTIVATED);
        Date now = new Date();
        account.setLastUpdated(now);
        account.setEditor("editor2");

        // 调用转换方法：将领域对象转换为持久化对象 AccountPO
        AccountPO po = AccountAssembler.toPO(account);
        // 验证结果
        assertNotNull(po);
        assertEquals(account.getId(), po.getId());
        assertEquals(account.getEmail(), po.getEmail());
        assertEquals(account.getStatus(), po.getStatus());
        assertEquals(account.getLastUpdated(), po.getLastUpdated());
        assertEquals(account.getEditor(), po.getEditor());
    }

    @Test
    void testToPO_withNullAccount() {
        // 如果领域对象为 null，则返回 null
        assertNull(AccountAssembler.toPO(null));
    }
}
