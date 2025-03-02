package com.example.emspdemo.repository.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.emspdemo.application.query.AccountQuery;
import com.example.emspdemo.assembler.AccountAssembler;
import com.example.emspdemo.assembler.TokenAssembler;
import com.example.emspdemo.domain.Account;
import com.example.emspdemo.domain.vo.Token;
import com.example.emspdemo.repository.dao.AccountDao;
import com.example.emspdemo.repository.mapper.AccountMapper;
import com.example.emspdemo.repository.mapper.TokenMapper;
import com.example.emspdemo.repository.po.AccountPO;
import com.example.emspdemo.repository.po.TokenPO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class AccountDaoImpl implements AccountDao {
    private final AccountMapper accountMapper;
    private final TokenMapper tokenMapper;

    public AccountDaoImpl(AccountMapper accountMapper, TokenMapper tokenMapper) {
        this.accountMapper = accountMapper;
        this.tokenMapper = tokenMapper;
    }


    @Override
    public void save(Account account) {
        AccountPO accountPO = AccountAssembler.toPO(account);
        if (accountPO.getId() == null) {
            accountMapper.insert(accountPO);
            account.setId(accountPO.getId());
            if (account.getTokens() != null && !account.getTokens().isEmpty()) {
                for (Token token : account.getTokens()) {
                    TokenPO tokenPO = TokenAssembler.toPO(token, account.getId());
                    tokenMapper.insert(tokenPO);
                }
            }
        } else {
            accountMapper.updateById(accountPO);
        }
    }

    @Override
    public Account findById(Long id) {
        AccountPO accountPO = accountMapper.selectById(id);
        if (accountPO == null) {
            return null;
        }
        QueryWrapper<TokenPO> tokenQuery = new QueryWrapper<>();
        tokenQuery.eq("external_id", id);
        List<TokenPO> tokenPOs = tokenMapper.selectList(tokenQuery);
        return AccountAssembler.toDomain(accountPO, tokenPOs);
    }

    @Override
    public Page<Account> queryAccounts(AccountQuery query, int pageNum, int pageSize) {
        QueryWrapper<AccountPO> qw = new QueryWrapper<>();
        if (query.getStartDate() != null) {
            qw.ge("last_updated", query.getStartDate());
        }
        if (query.getEndDate() != null) {
            qw.le("last_updated", query.getEndDate());
        }
        Page<AccountPO> poPage = accountMapper.selectPage(new Page<>(pageNum, pageSize), qw);
        List<Account> accounts = poPage.getRecords().stream().map(po -> {
            QueryWrapper<TokenPO> tokenQuery = new QueryWrapper<>();
            tokenQuery.eq("external_id", po.getId());
            List<TokenPO> tokenPOs = tokenMapper.selectList(tokenQuery);
            return AccountAssembler.toDomain(po, tokenPOs);
        }).collect(Collectors.toList());
        Page<Account> accountPage = new Page<>(poPage.getCurrent(), poPage.getSize(), poPage.getTotal());
        accountPage.setRecords(accounts);
        return accountPage;
    }

    @Override
    public Account findByEmail(String email) {
        QueryWrapper<AccountPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        AccountPO po = accountMapper.selectOne(queryWrapper);
        if (po == null) {
            return null;
        }
        // 根据 AccountPO 的 id 查询关联的 TokenPO 列表
        QueryWrapper<TokenPO> tokenQuery = new QueryWrapper<>();
        tokenQuery.eq("external_id", po.getId());
        List<TokenPO> tokenPOs = tokenMapper.selectList(tokenQuery);
        // 使用 AccountAssembler 将持久化对象转换为领域对象
        return AccountAssembler.toDomain(po, tokenPOs);
    }
}
