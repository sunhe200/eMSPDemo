package com.example.emspdemo.assembler;

import com.example.emspdemo.domain.Card;
import com.example.emspdemo.domain.enums.TokenProp;
import com.example.emspdemo.domain.vo.Token;
import com.example.emspdemo.presentation.dto.CardDTO;
import com.example.emspdemo.repository.po.CardPO;
import com.example.emspdemo.repository.po.TokenPO;

import java.util.List;
import java.util.stream.Collectors;

public class CardAssembler {
    /**
     * 将领域对象 Card 转换为 DTO 对象，用于对外展示
     */
    public static CardDTO toDTO(Card card) {
        if (card == null) {
            return null;
        }
        CardDTO dto = new CardDTO();
        dto.setId(card.getId());
        dto.setAccountId(card.getAccountId());
        dto.setStatus(card.getStatus());
        dto.setLastUpdated(card.getLastUpdated());
        dto.setEditor(card.getEditor());
        List<Token> tokens = card.getTokens();
        if (tokens != null) {
            for (Token token : tokens) {
                if (token.getPropName() != null) {
                    // 判断 token 的属性名是否为 "uid"
                    if (token.getPropName() == TokenProp.UID) {
                        dto.setUid(token.getPropValue());
                    }
                    // 判断 token 的属性名是否为 "visibleNumber"（映射为 CardDTO 的 cardNumber）
                    else if (token.getPropName() == TokenProp.VISIBLE_NUMBER) {
                        dto.setCardNumber(token.getPropValue());
                    }
                }
            }
        }
        return dto;
    }

    /**
     * 将持久化对象 CardPO 转换为领域对象 Card
     */
    public static Card toDomain(CardPO po, List<TokenPO> tokens) {
        if (po == null) {
            return null;
        }
        Card card = new Card();
        card.setId(po.getId());
        card.setAccountId(po.getAccountId());
        card.setStatus(po.getStatus());
        card.setLastUpdated(po.getLastUpdated());
        card.setEditor(po.getEditor());
        if (tokens != null && !tokens.isEmpty()) {
            List<Token> tokenVOs = tokens.stream()
                    .map(tp -> new Token(tp.getTokenType(), tp.getPropName(), tp.getPropValue()))
                    .collect(Collectors.toList());
            card.setTokens(tokenVOs);
        }
        return card;
    }

    /**
     * 将领域对象 Card 转换为持久化对象 CardPO
     */
    public static CardPO toPO(Card card) {
        if (card == null) {
            return null;
        }
        CardPO po = new CardPO();
        po.setId(card.getId());
        po.setAccountId(card.getAccountId());
        po.setStatus(card.getStatus());
        po.setLastUpdated(card.getLastUpdated());
        po.setEditor(card.getEditor());
        return po;
    }
}
