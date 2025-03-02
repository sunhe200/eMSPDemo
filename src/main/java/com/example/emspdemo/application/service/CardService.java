package com.example.emspdemo.application.service;

import com.example.emspdemo.application.command.AssignCardCommand;
import com.example.emspdemo.application.command.ChangeCardStatusCommand;
import com.example.emspdemo.application.command.CreateCardCommand;
import com.example.emspdemo.domain.Card;
import com.example.emspdemo.presentation.dto.CardDTO;
import com.example.emspdemo.domain.enums.CardStatus;

public interface CardService {
    /**
     * 创建 Card 聚合根
     */
    Card createCard(CreateCardCommand command);

    /**
     * 分配卡片到账户，返回更新后的 Card。
     */
    Card assignCardToAccount(Long cardId, AssignCardCommand command);

    /**
     * 修改卡片状态，返回修改后的 Card。
     */
    Card changeCardStatus(Long cardId, ChangeCardStatusCommand command);


}
