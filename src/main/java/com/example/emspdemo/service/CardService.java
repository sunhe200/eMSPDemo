package com.example.emspdemo.service;

import com.example.emspdemo.domain.dto.CardDTO;
import com.example.emspdemo.domain.enums.CardStatus;

public interface CardService {
    /**
     * 创建卡片。传入的 CardDTO 只需设置 cardNumber 字段，创建后返回完整的 DTO。
     */
    void createCard(CardDTO cardDTO);

    /**
     * 分配卡片到账户，返回更新后的 CardDTO。
     */
    void assignCardToAccount(Long cardId, Long accountId, String editor);

    /**
     * 修改卡片状态，返回修改后的 CardDTO。
     */
    void changeCardStatus(Long cardId, CardStatus status, String editor);


}
