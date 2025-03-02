package com.example.emspdemo.repository.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.emspdemo.application.query.AccountQuery;
import com.example.emspdemo.domain.Account;
import com.example.emspdemo.domain.enums.AccountStatus;
import com.example.emspdemo.domain.enums.TokenProp;
import com.example.emspdemo.domain.enums.TokenType;
import com.example.emspdemo.domain.vo.Token;
import com.example.emspdemo.repository.mapper.AccountMapper;
import com.example.emspdemo.repository.mapper.TokenMapper;
import com.example.emspdemo.repository.po.AccountPO;
import com.example.emspdemo.repository.po.TokenPO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class AccountDaoImplTest {

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private TokenMapper tokenMapper;

    @InjectMocks
    private AccountDaoImpl accountDaoImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave_InsertNewAccount() {
        // 创建一个新的 Account 对象（id 为 null），并添加一个 token
        Account account = new Account();
        account.setEmail("new@example.com");
        account.setEditor("admin");
        account.setStatus(AccountStatus.CREATED);
        account.setLastUpdated(new Date());
        Token token = new Token(TokenType.EMAID, TokenProp.CONTRACT_ID, "contract123");
        account.getTokens().add(token);

        // 模拟 accountMapper.insert 设置 id
        doAnswer(invocation -> {
            AccountPO po = invocation.getArgument(0);
            po.setId(1L);
            return 1;
        }).when(accountMapper).insert(any(AccountPO.class));
        // 模拟 tokenMapper.insert 返回成功
        when(tokenMapper.insert(any(TokenPO.class))).thenReturn(1);

        accountDaoImpl.save(account);

        // 验证新插入时，account id 应该被设置
        assertEquals(1L, account.getId());
        verify(accountMapper, times(1)).insert(any(AccountPO.class));
        verify(tokenMapper, times(account.getTokens().size())).insert(any(TokenPO.class));
    }

    @Test
    void testSave_UpdateAccount() {
        // 创建一个已有 id 的 Account 对象
        Account account = new Account();
        account.setId(2L);
        account.setEmail("update@example.com");
        account.setEditor("admin");
        account.setStatus(AccountStatus.CREATED);
        account.setLastUpdated(new Date());
        // 不设置 token 列表，此时只做更新操作

        accountDaoImpl.save(account);
        // 验证调用 updateById 而不是 insert
        verify(accountMapper, times(1)).updateById(any(AccountPO.class));
        verify(tokenMapper, never()).insert(any(TokenPO.class));
    }

    @Test
    void testFindById_NotFound() {
        // 模拟 accountMapper.selectById 返回 null
        when(accountMapper.selectById(1L)).thenReturn(null);
        Account account = accountDaoImpl.findById(1L);
        assertNull(account);
    }

    @Test
    void testFindById_Found() {
        // 构造 AccountPO
        AccountPO po = new AccountPO();
        po.setId(1L);
        po.setEmail("found@example.com");
        po.setEditor("admin");
        po.setStatus(AccountStatus.CREATED);
        Date now = new Date();
        po.setLastUpdated(now);

        when(accountMapper.selectById(1L)).thenReturn(po);

        // 构造 TokenPO 列表
        TokenPO tokenPO = new TokenPO();
        tokenPO.setExternalId(1L);
        tokenPO.setTokenType(TokenType.EMAID);
        tokenPO.setPropName(TokenProp.CONTRACT_ID);
        tokenPO.setPropValue("contract456");
        List<TokenPO> tokenPOs = Collections.singletonList(tokenPO);
        // 当 selectList 调用时返回 tokenPOs
        when(tokenMapper.selectList(any(QueryWrapper.class))).thenReturn(tokenPOs);

        Account account = accountDaoImpl.findById(1L);
        assertNotNull(account);
        assertEquals(1L, account.getId());
        assertEquals("found@example.com", account.getEmail());
        assertEquals("admin", account.getEditor());
        // 检查 tokens
        List<Token> tokens = account.getTokens();
        assertNotNull(tokens);
        assertEquals(1, tokens.size());
        Token token = tokens.get(0);
        assertEquals("contract456", token.getPropValue());
    }

    @Test
    void testQueryAccounts() {
        // 构造 AccountQuery
        AccountQuery query = new AccountQuery();
        query.setStartDate(new Date(System.currentTimeMillis() - 10000));
        query.setEndDate(new Date());

        // 构造分页返回的 AccountPO Page
        AccountPO po = new AccountPO();
        po.setId(1L);
        po.setEmail("page@example.com");
        po.setEditor("admin");
        po.setStatus(AccountStatus.CREATED);
        Date now = new Date();
        po.setLastUpdated(now);

        Page<AccountPO> poPage = new Page<>(0, 10, 1);
        poPage.setRecords(Collections.singletonList(po));

        when(accountMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(poPage);
        // 模拟 tokenMapper.selectList 返回空列表
        when(tokenMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());

        Page<Account> accountPage = accountDaoImpl.queryAccounts(query, 0, 10);
        assertNotNull(accountPage);
        assertEquals(1, accountPage.getTotal());
        assertEquals(1, accountPage.getRecords().size());
        Account account = accountPage.getRecords().get(0);
        assertEquals(1L, account.getId());
        assertEquals("page@example.com", account.getEmail());
    }

    @Test
    void testFindByEmail_NotFound() {
        // 模拟未查到数据
        when(accountMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);
        Account account = accountDaoImpl.findByEmail("notfound@example.com");
        assertNull(account);
    }

    @Test
    void testFindByEmail_Found() {
        // 构造 AccountPO
        AccountPO po = new AccountPO();
        po.setId(1L);
        po.setEmail("email@example.com");
        po.setEditor("admin");
        po.setStatus(AccountStatus.CREATED);
        Date now = new Date();
        po.setLastUpdated(now);
        when(accountMapper.selectOne(any(QueryWrapper.class))).thenReturn(po);

        // 构造 TokenPO 列表
        TokenPO tokenPO = new TokenPO();
        tokenPO.setExternalId(1L);
        tokenPO.setTokenType(TokenType.EMAID);
        tokenPO.setPropName(TokenProp.CONTRACT_ID);
        tokenPO.setPropValue("contract789");
        List<TokenPO> tokenPOs = Collections.singletonList(tokenPO);
        when(tokenMapper.selectList(any(QueryWrapper.class))).thenReturn(tokenPOs);

        Account account = accountDaoImpl.findByEmail("email@example.com");
        assertNotNull(account);
        assertEquals(1L, account.getId());
        assertEquals("email@example.com", account.getEmail());
        List<Token> tokens = account.getTokens();
        assertNotNull(tokens);
        assertEquals(1, tokens.size());
        Token token = tokens.get(0);
        assertEquals("contract789", token.getPropValue());
    }
}
