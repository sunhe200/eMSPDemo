package com.example.emspdemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.emspdemo.domain.convert.AccountConvert;
import com.example.emspdemo.domain.convert.CardConvert;
import com.example.emspdemo.domain.dto.AccountCardDTO;
import com.example.emspdemo.domain.dto.AccountCardQueryDTO;
import com.example.emspdemo.domain.dto.AccountDTO;
import com.example.emspdemo.domain.dto.CardDTO;
import com.example.emspdemo.domain.po.AccountPO;
import com.example.emspdemo.domain.po.CardPO;
import com.example.emspdemo.repository.AccountMapper;
import com.example.emspdemo.repository.CardMapper;
import com.example.emspdemo.service.AccountCardService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountCardServiceImpl implements AccountCardService {
    private final AccountMapper accountMapper;
    private final CardMapper cardMapper;

    public AccountCardServiceImpl(AccountMapper accountMapper, CardMapper cardMapper) {
        this.accountMapper = accountMapper;
        this.cardMapper = cardMapper;
    }

    @Override
    public Page<AccountCardDTO> getAccountWithCardByLastUpdated(AccountCardQueryDTO query, int page, int size) {
        // 分页查询 Account（按 lastUpdated 在 [start, end] 范围内过滤）
        // 注意：Page 构造方法中的页码是从 1 开始的
        Page<AccountPO> accountPage = new Page<>(page, size);
        LambdaQueryWrapper<AccountPO> accountWrapper = new LambdaQueryWrapper<>();
        accountWrapper.between(AccountPO::getLastUpdated, query.getStartDate(), query.getEndDate());
        Page<AccountPO> resultPage = accountMapper.selectPage(accountPage, accountWrapper);

        // 对于每个 Account，查询其所有 Card 信息
        List<AccountCardDTO> records = resultPage.getRecords().stream().map(account -> {
            AccountDTO accountDTO = AccountConvert.INSTANCE.po2Dto(account);
            LambdaQueryWrapper<CardPO> cardWrapper = new LambdaQueryWrapper<>();
            cardWrapper.eq(CardPO::getAccountId, account.getId());
            List<CardPO> cards = cardMapper.selectList(cardWrapper);
            List<CardDTO> cardDTOs = cards.stream()
                    .map(CardConvert.INSTANCE::po2Dto)
                    .collect(Collectors.toList());
            AccountCardDTO tokenDTO = new AccountCardDTO();
            tokenDTO.setAccount(accountDTO);
            tokenDTO.setCards(cardDTOs);
            return tokenDTO;
        }).collect(Collectors.toList());

        Page<AccountCardDTO> dtoPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        dtoPage.setRecords(records);
        return dtoPage;
    }

}
