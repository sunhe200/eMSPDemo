package com.example.emspdemo.repository.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.emspdemo.assembler.CardAssembler;
import com.example.emspdemo.assembler.TokenAssembler;
import com.example.emspdemo.domain.Card;
import com.example.emspdemo.domain.vo.Token;
import com.example.emspdemo.repository.dao.CardDao;
import com.example.emspdemo.repository.mapper.CardMapper;
import com.example.emspdemo.repository.mapper.TokenMapper;
import com.example.emspdemo.repository.po.CardPO;
import com.example.emspdemo.repository.po.TokenPO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CardDaoImpl implements CardDao {
    private final CardMapper cardPOMapper;
    private final TokenMapper tokenMapper;

    public CardDaoImpl(CardMapper cardPOMapper, TokenMapper tokenMapper) {
        this.cardPOMapper = cardPOMapper;
        this.tokenMapper = tokenMapper;
    }

    @Override
    public void save(Card card) {
        CardPO po = CardAssembler.toPO(card);
        if (po.getId() == null) {
            cardPOMapper.insert(po);
            card.setId(po.getId());
            if (card.getTokens() != null && !card.getTokens().isEmpty()) {
                for (Token token : card.getTokens()) {
                    TokenPO tokenPO = TokenAssembler.toPO(token, card.getId());
                    tokenMapper.insert(tokenPO);
                }
            }
        } else {
            cardPOMapper.updateById(po);
        }
    }

    @Override
    public Card findById(Long id) {
        CardPO cardPO = cardPOMapper.selectById(id);
        if (cardPO == null) {
            return null;
        }
        // 查询与该 Card 关联的 TokenPO 列表
        QueryWrapper<TokenPO> tokenQuery = new QueryWrapper<>();
        tokenQuery.eq("external_id", cardPO.getId());
        List<TokenPO> tokens = tokenMapper.selectList(tokenQuery);
        // 使用 CardAssembler 将持久化对象转换为领域对象，并装配 token 列表
        return CardAssembler.toDomain(cardPO, tokens);
    }
}
