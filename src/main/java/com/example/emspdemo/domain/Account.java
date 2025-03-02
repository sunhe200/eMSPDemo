package com.example.emspdemo.domain;

import cn.hutool.core.util.IdUtil;
import com.example.emspdemo.domain.enums.AccountStatus;
import com.example.emspdemo.domain.enums.TokenProp;
import com.example.emspdemo.domain.enums.TokenType;
import com.example.emspdemo.domain.event.AccountCreatedEvent;
import com.example.emspdemo.domain.event.AccountStatusChangedEvent;
import com.example.emspdemo.domain.event.DomainEvent;
import com.example.emspdemo.domain.vo.Token;
import com.example.emspdemo.util.EMAIDUtil;
import com.example.emspdemo.util.EmailUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class Account {
    private Long id;
    private String email;
    private AccountStatus status;
    private Date lastUpdated;
    private String editor;
    private List<Token> tokens = new ArrayList<>();
    // 用于记录领域事件
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * 工厂方法创建 Account，同时生成 contract ID 并记录为 EMAID token
     */
    public static Account create(String email, String editor) {
        EmailUtil.checkEmailFormat(email);
        Account account = new Account();
        account.email = email;
        account.status = AccountStatus.CREATED;
        account.lastUpdated = new Date();
        account.editor = editor;
        // 生成 EMAID
        String emaid = EMAIDUtil.generateEMAID();
        // 同时在 tokens 中添加 EMAID 类型的 token，业务上表示合同编号
        account.tokens.add(new Token(TokenType.EMAID, TokenProp.CONTRACT_ID, emaid));
        // 触发创建事件
        account.addDomainEvent(new AccountCreatedEvent(account, IdUtil.fastSimpleUUID(), new Date()));
        return account;
    }

    public void changeStatus(AccountStatus newStatus, String editor) {
        this.status = newStatus;
        this.lastUpdated = new Date();
        this.editor = editor;
        // 触发事件
        addDomainEvent(new AccountStatusChangedEvent(this, IdUtil.fastSimpleUUID(), new Date(), newStatus));
    }

    public void clearEvent() {
        domainEvents.clear();
    }

    private void addDomainEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }
}
