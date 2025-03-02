package com.example.emspdemo.assembler;

import com.example.emspdemo.domain.vo.Token;
import com.example.emspdemo.repository.po.TokenPO;

public class TokenAssembler {
    public static TokenPO toPO(Token token, Long externalId) {
        if (token == null) {
            return null;
        }
        TokenPO po = new TokenPO();
        po.setExternalId(externalId);
        po.setTokenType(token.getTokenType());
        po.setPropName(token.getPropName());
        po.setPropValue(token.getPropValue());
        return po;
    }
}
