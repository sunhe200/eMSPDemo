package com.example.emspdemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.emspdemo.domain.convert.AccountConvert;
import com.example.emspdemo.domain.dto.AccountDTO;
import com.example.emspdemo.domain.enums.AccountStatus;
import com.example.emspdemo.domain.event.AccountCreatedEvent;
import com.example.emspdemo.domain.event.AccountStatusChangedEvent;
import com.example.emspdemo.domain.po.AccountPO;
import com.example.emspdemo.repository.AccountMapper;
import com.example.emspdemo.service.AccountService;
import com.example.emspdemo.util.EMAIDUtil;
import com.example.emspdemo.util.EmailUtil;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountMapper accountMapper;
    private final ApplicationEventPublisher publisher;

    public AccountServiceImpl(AccountMapper accountMapper, ApplicationEventPublisher publisher) {
        this.accountMapper = accountMapper;
        this.publisher = publisher;
    }

    @Override
    public void createAccount(AccountDTO accountDTO) {
        EmailUtil.checkEmailFormat(accountDTO.getEmail());
        AccountPO po = AccountConvert.INSTANCE.dto2Po(accountDTO);
        po.setStatus(AccountStatus.CREATED);
        String contractId = EMAIDUtil.normalizeEMAID(EMAIDUtil.generateEMAID());
        po.setContractId(contractId);
        po.setLastUpdated(new Date());
        accountMapper.insert(po);
        LambdaQueryWrapper<AccountPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountPO::getEmail, accountDTO.getEmail());
        publisher.publishEvent(
                new AccountCreatedEvent(AccountConvert.INSTANCE.po2Dto(accountMapper.selectOne(wrapper))));
    }

    @Override
    public void changeAccountStatus(Long accountId, AccountStatus status, String editor) {
        AccountPO accountPO = accountMapper.selectById(accountId);
        if (accountPO == null) {
            throw new RuntimeException("Account not found");
        }
        accountPO.setStatus(status);
        accountPO.setEditor(editor);
        accountPO.setLastUpdated(new Date());
        accountMapper.updateById(accountPO);
        publisher.publishEvent(
                new AccountStatusChangedEvent(AccountConvert.INSTANCE.po2Dto(
                        accountMapper.selectById(accountId))));

    }
}
