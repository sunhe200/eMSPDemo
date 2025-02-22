package com.example.emspdemo.controller;

import cn.hutool.core.lang.Assert;
import com.example.emspdemo.domain.common.BaseRequest;
import com.example.emspdemo.domain.common.BaseResponse;
import com.example.emspdemo.domain.dto.CardDTO;
import com.example.emspdemo.domain.enums.CardStatus;
import com.example.emspdemo.service.CardService;
import com.example.emspdemo.util.TraceHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/card")
@Slf4j
public class CardController {
    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/")
    public BaseResponse<Void> createCard(@RequestBody BaseRequest<CardDTO> request) {
        log.info("Create Card Start - TraceID: {} - parameter：{}", TraceHolder.get(), request);
        Assert.notNull(request, "Parameter is NULL！");
        cardService.createCard(request.getRequestData());
        log.info("Create Card End - TraceID: {}", TraceHolder.get());
        return BaseResponse.success();
    }

    @PutMapping("/{cardId}/assign/{accountId}")
    public BaseResponse<Void> assignCardToAccount(
            @PathVariable Long cardId, @PathVariable Long accountId, @RequestParam String editor) {
        log.info("Assign Card To Account Start - TraceID: {} - Card ID:{}, Account ID：{}, Editor: {}", TraceHolder.get(), cardId, accountId, editor);
        Assert.notNull(cardId, "Card ID is NUll!");
        Assert.notNull(accountId, "Account ID is NUll!");
        Assert.notNull(editor, "Editor is NULL！");
        cardService.assignCardToAccount(cardId, accountId, editor);
        log.info("Assign Card To Account End - TraceID: {}", TraceHolder.get());
        return BaseResponse.success();
    }

    @PutMapping("/{cardId}/status")
    public BaseResponse<Void> changeCardStatus(
            @PathVariable Long cardId, @RequestParam CardStatus status, @RequestParam String editor) {
        log.info("Change Card Status Start - TraceID: {} - Card ID:{}, Card Status：{}, Editor: {}", TraceHolder.get(), cardId, status, editor);
        Assert.notNull(cardId, "Card ID is NUll!");
        Assert.notNull(status, "Card Status is NUll!");
        Assert.notNull(editor, "Editor is NULL！");
        cardService.changeCardStatus(cardId, status, editor);
        log.info("Change Card Status End - TraceID: {}", TraceHolder.get());
        return BaseResponse.success();
    }
}
