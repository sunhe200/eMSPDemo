package com.example.emspdemo.presentation.controller;

import cn.hutool.core.lang.Assert;
import com.example.emspdemo.application.command.AssignCardCommand;
import com.example.emspdemo.application.command.ChangeCardStatusCommand;
import com.example.emspdemo.application.command.CreateCardCommand;
import com.example.emspdemo.assembler.CardAssembler;
import com.example.emspdemo.presentation.dto.common.BaseRequest;
import com.example.emspdemo.presentation.dto.common.BaseResponse;
import com.example.emspdemo.presentation.dto.CardDTO;
import com.example.emspdemo.application.service.CardService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/card")
@Slf4j
@Validated
public class CardController {
    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse<CardDTO> createCard(@Valid @RequestBody BaseRequest<CreateCardCommand> request) {
        log.info("Create Card Start - parameter：{}", request);
        Assert.notNull(request, "Parameter is NULL！");
        Assert.notNull(request.getRequestData(), "Parameter is NULL！");
        CardDTO cardDTO = CardAssembler.toDTO(cardService.createCard(request.getRequestData()));
        log.info("Create Card End - RequestNo: {}", request.getRequestNo());
        return BaseResponse.success(cardDTO);
    }

    @PatchMapping("/{cardId}/assign")
    public BaseResponse<CardDTO> assignCardToAccount(
            @PathVariable Long cardId, @Valid @RequestBody BaseRequest<AssignCardCommand> request) {
        log.info("Assign Card To Account Start - Card ID: {} - parameter：{}", cardId, request);
        Assert.notNull(cardId, "Card ID is NULL!");
        Assert.notNull(request, "Parameter is NULL！");
        Assert.notNull(request.getRequestData(), "Parameter is NULL！");
        CardDTO cardDTO = CardAssembler.toDTO(cardService.assignCardToAccount(cardId, request.getRequestData()));
        log.info("Assign Card To Account End - RequestNo: {}", request.getRequestNo());
        return BaseResponse.success(cardDTO);
    }

    @PatchMapping("/{cardId}/status")
    public BaseResponse<CardDTO> changeCardStatus(
            @PathVariable Long cardId, @Valid @RequestBody BaseRequest<ChangeCardStatusCommand> request) {
        log.info("Change Card Status Start - Card ID:{} - parameter：{}",  cardId, request);
        Assert.notNull(cardId, "Card ID is NUll!");
        Assert.notNull(request, "Parameter is NULL！");
        Assert.notNull(request.getRequestData(), "Parameter is NULL！");
        CardDTO cardDTO = CardAssembler.toDTO(cardService.changeCardStatus(cardId, request.getRequestData()));
        log.info("Change Card Status End - RequestNo: {}", request.getRequestNo());
        return BaseResponse.success(cardDTO);
    }
}
