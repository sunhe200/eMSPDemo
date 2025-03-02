package com.example.emspdemo.repository.dao;

import com.example.emspdemo.domain.Card;

public interface CardDao {
    /**
     * 保存 Card 聚合根，如果 Card.id 为 null 则插入，否则更新。
     */
    void save(Card card);

    /**
     * 根据 Card 的 id 查找 Card 聚合根，如果不存在返回 null。
     */
    Card findById(Long id);
}
