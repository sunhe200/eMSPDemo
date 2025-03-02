package com.example.emspdemo.assembler;

import com.example.emspdemo.domain.Account;
import com.example.emspdemo.domain.enums.TokenProp;
import com.example.emspdemo.domain.vo.Token;
import com.example.emspdemo.presentation.dto.AccountDTO;
import com.example.emspdemo.repository.po.AccountPO;
import com.example.emspdemo.repository.po.TokenPO;

import java.util.List;
import java.util.stream.Collectors;

public class AccountAssembler {

    /**
     * 将领域层的 Account 转换为对外展示的 DTO。
     */
    public static AccountDTO toDTO(Account account) {
        if (account == null) {
            return null;
        }
        AccountDTO dto = new AccountDTO();
        dto.setId(account.getId());
        dto.setEmail(account.getEmail());
        dto.setStatus(account.getStatus());
        dto.setLastUpdated(account.getLastUpdated());
        dto.setEditor(account.getEditor());
        List<Token> tokens = account.getTokens();
        String contractId = null;
        if (tokens != null) {
            contractId = tokens.stream()
                    .filter(token -> token.getPropName() != null &&
                            token.getPropName() == TokenProp.CONTRACT_ID)
                    .map(Token::getPropValue)
                    .findFirst()
                    .orElse(null);
        }
        dto.setContractId(contractId);
        return dto;
    }

    /**
     * 将持久化对象 AccountPO 和关联的 TokenPO 列表转换为领域对象 Account。
     */
    public static Account toDomain(AccountPO po, List<TokenPO> tokenPOs) {
        if (po == null) {
            return null;
        }
        Account account = new Account();
        account.setId(po.getId());
        account.setEmail(po.getEmail());
        account.setStatus(po.getStatus());
        account.setLastUpdated(po.getLastUpdated());
        account.setEditor(po.getEditor());
        // 将 TokenPO 列表转换为 TokenVO 列表
        if (tokenPOs != null && !tokenPOs.isEmpty()) {
            List<Token> tokens = tokenPOs.stream()
                    .map(tp -> new Token(tp.getTokenType(), tp.getPropName(), tp.getPropValue()))
                    .collect(Collectors.toList());
            account.setTokens(tokens);
        }
        return account;
    }

    /**
     * 将领域对象 Account 转换为持久化对象 AccountPO，用于数据存储。
     */
    public static AccountPO toPO(Account account) {
        if (account == null) {
            return null;
        }
        AccountPO po = new AccountPO();
        po.setId(account.getId());
        po.setEmail(account.getEmail());
        po.setStatus(account.getStatus());
        po.setLastUpdated(account.getLastUpdated());
        po.setEditor(account.getEditor());
        return po;
    }
}

