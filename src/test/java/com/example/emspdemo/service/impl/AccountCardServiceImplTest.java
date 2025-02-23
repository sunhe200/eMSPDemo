package com.example.emspdemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.emspdemo.domain.convert.AccountConvert;
import com.example.emspdemo.domain.convert.CardConvert;
import com.example.emspdemo.domain.dto.AccountCardDTO;
import com.example.emspdemo.domain.dto.AccountCardQueryDTO;
import com.example.emspdemo.domain.po.AccountPO;
import com.example.emspdemo.domain.po.CardPO;
import com.example.emspdemo.repository.AccountMapper;
import com.example.emspdemo.repository.CardMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountCardServiceImplTest {

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private AccountCardServiceImpl accountCardService;

    @Test
    public void testGetAccountWithCardByLastUpdated() {
        // 构造查询条件，设置开始和结束时间
        AccountCardQueryDTO query = new AccountCardQueryDTO();
        Date now = new Date();
        // 模拟查询时间范围：当前时间前后各1小时
        query.setStartDate(new Date(now.getTime() - 3600 * 1000));
        query.setEndDate(new Date(now.getTime() + 3600 * 1000));

        int page = 1;
        int size = 10;

        // 模拟返回的 AccountPO 列表
        AccountPO account1 = new AccountPO();
        account1.setId(1L);
        account1.setEmail("account1@example.com");
        account1.setLastUpdated(now);

        AccountPO account2 = new AccountPO();
        account2.setId(2L);
        account2.setEmail("account2@example.com");
        account2.setLastUpdated(now);

        List<AccountPO> accountList = Arrays.asList(account1, account2);

        // 构造 accountMapper.selectPage 返回的分页数据
        Page<AccountPO> accountPage = new Page<>(page, size);
        accountPage.setRecords(accountList);
        accountPage.setTotal(2);
        when(accountMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(accountPage);

        // 模拟每个 Account 对应的 CardPO 列表
        CardPO card1 = new CardPO();
        card1.setId(101L);
        card1.setAccountId(1L);
        card1.setCardNumber("card101");

        CardPO card2 = new CardPO();
        card2.setId(102L);
        card2.setAccountId(2L);
        card2.setCardNumber("card102");

        // 按照 accountList 的顺序，依次返回每个账号的卡片列表
        when(cardMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(card1))
                .thenReturn(Collections.singletonList(card2));

        // 调用待测方法
        Page<AccountCardDTO> resultPage = accountCardService.getAccountWithCardByLastUpdated(query, page, size);

        // 断言分页信息
        assertNotNull(resultPage, "返回的分页数据不应为空");
        assertEquals(2, resultPage.getTotal(), "总记录数应为2");
        assertEquals(page, resultPage.getCurrent(), "当前页码不符");
        assertEquals(size, resultPage.getSize(), "每页记录数不符");

        // 断言转换后的数据
        List<AccountCardDTO> records = resultPage.getRecords();
        assertEquals(2, records.size(), "记录数量应为2");

        // 验证第1个账号对应的 DTO
        AccountCardDTO dto1 = records.get(0);
        assertNotNull(dto1.getAccount(), "AccountCardDTO.account 不能为空");
        assertEquals(1L, dto1.getAccount().getId(), "第1个账号ID应为1");
        // 通过 CardConvert.INSTANCE.po2Dto 进行转换，默认拷贝 id 字段
        assertNotNull(dto1.getCards(), "第1个账号的卡片列表不应为空");
        assertEquals(1, dto1.getCards().size(), "第1个账号应有1个卡片");
        assertEquals(101L, dto1.getCards().get(0).getId(), "卡片ID不符");

        // 验证第2个账号对应的 DTO
        AccountCardDTO dto2 = records.get(1);
        assertNotNull(dto2.getAccount(), "AccountCardDTO.account 不能为空");
        assertEquals(2L, dto2.getAccount().getId(), "第2个账号ID应为2");
        assertNotNull(dto2.getCards(), "第2个账号的卡片列表不应为空");
        assertEquals(1, dto2.getCards().size(), "第2个账号应有1个卡片");
        assertEquals(102L, dto2.getCards().get(0).getId(), "卡片ID不符");
    }
}
