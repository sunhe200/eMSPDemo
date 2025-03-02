package com.example.emspdemo.presentation.controller;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.example.emspdemo.EMspDemoApplication;
import com.example.emspdemo.application.command.AssignCardCommand;
import com.example.emspdemo.application.command.ChangeCardStatusCommand;
import com.example.emspdemo.application.command.CreateCardCommand;
import com.example.emspdemo.application.service.CardService;
import com.example.emspdemo.domain.Card;
import com.example.emspdemo.domain.enums.CardStatus;
import com.example.emspdemo.presentation.dto.CardDTO;
import com.example.emspdemo.presentation.dto.common.BaseRequest;
import com.example.emspdemo.presentation.dto.common.PageRequest;
import com.example.emspdemo.presentation.dto.common.PageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CardController.class,
        excludeAutoConfiguration = {MybatisPlusAutoConfiguration.class})
@ContextConfiguration(classes = {CardController.class, CardControllerTest.TestConfig.class})
public class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CardService cardService; // 使用 TestConfig 手动提供模拟 Bean

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CardService cardService() {
            return Mockito.mock(CardService.class);
        }
    }

    @Test
    void testCreateCard() throws Exception {
        // 构造 CreateCardCommand 并包装到 BaseRequest 中
        CreateCardCommand command = new CreateCardCommand();
        command.setEditor("admin");
        // 此处假设 createCard 只需要 editor 参数用于生成 RFID 信息
        BaseRequest<CreateCardCommand> request = new BaseRequest<>();
        request.setRequestNo("REQ-001");
        request.setRequestData(command);

        // 构造返回的 Card 领域对象
        Card card = new Card();
        card.setId(1L);
        card.setEditor("admin");
        card.setStatus(CardStatus.CREATED);
        card.setLastUpdated(new Date());
        // 此处 token 列表略过

        Mockito.when(cardService.createCard(Mockito.any(CreateCardCommand.class)))
                .thenReturn(card);

        mockMvc.perform(post("/card")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.editor", is("admin")));
    }

    @Test
    void testAssignCardToAccount() throws Exception {
        // 构造 AssignCardCommand 并包装到 BaseRequest 中
        AssignCardCommand command = new AssignCardCommand();
        command.setAccountId(100L);
        command.setEditor("operator");
        BaseRequest<AssignCardCommand> request = new BaseRequest<>();
        request.setRequestNo("REQ-002");
        request.setRequestData(command);

        // 构造返回的 Card 领域对象
        Card card = new Card();
        card.setId(1L);
        card.setAccountId(100L);
        card.setEditor("operator");
        card.setStatus(CardStatus.CREATED);  // 假设分配后状态仍为 CREATED 或由 assignToAccount 方法修改内部状态
        card.setLastUpdated(new Date());

        Mockito.when(cardService.assignCardToAccount(Mockito.eq(1L), Mockito.any(AssignCardCommand.class)))
                .thenReturn(card);

        mockMvc.perform(patch("/card/1/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.accountId", is(100)))
                .andExpect(jsonPath("$.data.editor", is("operator")));
    }

    @Test
    void testChangeCardStatus() throws Exception {
        // 构造 ChangeCardStatusCommand 并包装到 BaseRequest 中
        ChangeCardStatusCommand command = new ChangeCardStatusCommand();
        command.setNewStatus(CardStatus.ACTIVATED);
        command.setEditor("operator");
        BaseRequest<ChangeCardStatusCommand> request = new BaseRequest<>();
        request.setRequestNo("REQ-003");
        request.setRequestData(command);

        // 构造返回的 Card 领域对象
        Card card = new Card();
        card.setId(1L);
        card.setEditor("operator");
        card.setStatus(CardStatus.ACTIVATED);
        card.setLastUpdated(new Date());
        // accountId 可为空或已有值，此处不设置

        Mockito.when(cardService.changeCardStatus(Mockito.eq(1L), Mockito.any(ChangeCardStatusCommand.class)))
                .thenReturn(card);

        mockMvc.perform(patch("/card/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.status", is("ACTIVATED")))
                .andExpect(jsonPath("$.data.editor", is("operator")));
    }
}
