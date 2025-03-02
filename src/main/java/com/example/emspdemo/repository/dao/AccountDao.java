package com.example.emspdemo.repository.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.emspdemo.application.query.AccountQuery;
import com.example.emspdemo.domain.Account;

public interface AccountDao {
    void save(Account account);
    Account findById(Long id);
    Page<Account> queryAccounts(AccountQuery query, int pageNum, int pageSize);
    Account findByEmail(String email);
}
