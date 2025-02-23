package com.example.emspdemo.controller;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.example.emspdemo.domain.common.BaseRequest;
import com.example.emspdemo.domain.dto.CardDTO;
import com.example.emspdemo.domain.enums.CardStatus;
import com.example.emspdemo.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.annotation.Resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CardController.class,
        excludeAutoConfiguration = {MybatisPlusAutoConfiguration.class})
@ContextConfiguration(classes = {CardController.class, CardControllerTest.MockConfig.class})
public class CardControllerTest {

    @Resource
    private MockMvc mockMvc;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private CardService cardService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public CardService cardService() {
            return Mockito.mock(CardService.class);
        }
    }

    @Test
    void testCreateCard() throws Exception {
        // 构造请求数据 BaseRequest<CardDTO>
        CardDTO cardDTO = new CardDTO();
        // 可根据需要设置 cardDTO 的属性
        BaseRequest<CardDTO> request = new BaseRequest<>();
        request.setRequestData(cardDTO);
        String jsonRequest = objectMapper.writeValueAsString(request);

        // 调用 POST /card/ 接口
        mockMvc.perform(post("/card/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));

        // 验证 cardService.createCard 方法被调用
        verify(cardService).createCard(any(CardDTO.class));
    }

    @Test
    void testAssignCardToAccount() throws Exception {
        Long cardId = 1L;
        Long accountId = 2L;
        String editor = "testEditor";

        // 调用 PUT /card/{cardId}/assign/{accountId}?editor=...
        mockMvc.perform(put("/card/{cardId}/assign/{accountId}", cardId, accountId)
                        .param("editor", editor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));

        // 验证 cardService.assignCardToAccount 方法被调用
        verify(cardService).assignCardToAccount(eq(cardId), eq(accountId), eq(editor));
    }

    @Test
    void testChangeCardStatus() throws Exception {
        Long cardId = 1L;
        String status = "ACTIVATED"; // 假设 "ACTIVATED" 是 CardStatus 枚举中的一个有效值
        String editor = "testEditor";

        // 调用 PUT /card/{cardId}/status?status=...&editor=...
        mockMvc.perform(put("/card/{cardId}/status", cardId)
                        .param("status", status)
                        .param("editor", editor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));

        // 验证 cardService.changeCardStatus 方法被调用
        verify(cardService).changeCardStatus(eq(cardId), eq(CardStatus.valueOf(status)), eq(editor));
    }
}
