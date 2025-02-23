package com.example.emspdemo.controller;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.emspdemo.domain.common.BaseRequest;
import com.example.emspdemo.domain.common.PageRequest;
import com.example.emspdemo.domain.dto.AccountCardDTO;
import com.example.emspdemo.domain.dto.AccountCardQueryDTO;
import com.example.emspdemo.domain.dto.AccountDTO;
import com.example.emspdemo.domain.enums.AccountStatus;
import com.example.emspdemo.service.AccountCardService;
import com.example.emspdemo.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AccountController.class,
        excludeAutoConfiguration = {MybatisPlusAutoConfiguration.class})
// 显式指定只加载 AccountController 与 MockConfig，从而不扫描主应用 EMspDemoApplication
@ContextConfiguration(classes = {AccountController.class, AccountControllerTest.MockConfig.class})
public class AccountControllerTest {

    @Resource
    private MockMvc mockMvc;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private AccountService accountService;

    @Resource
    private AccountCardService accountCardService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public AccountService accountService() {
            return Mockito.mock(AccountService.class);
        }
        @Bean
        public AccountCardService accountCardService() {
            return Mockito.mock(AccountCardService.class);
        }
    }

    @Test
    void testCreateAccount() throws Exception {
        // 构造请求数据 BaseRequest<AccountDTO>
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setEmail("test@example.com");
        BaseRequest<AccountDTO> request = new BaseRequest<>();
        request.setRequestData(accountDTO);
        String jsonRequest = objectMapper.writeValueAsString(request);

        // 调用 POST /account/ 接口
        mockMvc.perform(post("/account/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));

        // 验证 accountService.createAccount 方法被调用
        verify(accountService).createAccount(any(AccountDTO.class));
    }

    @Test
    void testChangeAccountStatus() throws Exception {
        Long accountId = 1L;
        String status = "ACTIVATED"; // 假设枚举名称
        String editor = "testEditor";

        // 调用 PUT /account/{id}/status 接口
        mockMvc.perform(put("/account/{id}/status", accountId)
                        .param("status", status)
                        .param("editor", editor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));

        // 验证 accountService.changeAccountStatus 方法被调用
        verify(accountService).changeAccountStatus(eq(accountId), eq(AccountStatus.valueOf(status)), eq(editor));
    }

    @Test
    void testGetAccountWithCard() throws Exception {
        // 构造 PageRequest<AccountCardQueryDTO>
        AccountCardQueryDTO queryDTO = new AccountCardQueryDTO();
        queryDTO.setStartDate(new Date(0));
        queryDTO.setEndDate(new Date());
        PageRequest<AccountCardQueryDTO> request = new PageRequest<>();
        request.setPageNo(1);
        request.setPageSize(10);
        request.setRequestData(queryDTO);
        String jsonRequest = objectMapper.writeValueAsString(request);

        // 构造返回分页数据
        Page<AccountCardDTO> page = new Page<>(1, 10);
        page.setTotal(1);
        AccountCardDTO dto = new AccountCardDTO();
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setEmail("test@example.com");
        dto.setAccount(accountDTO);
        dto.setCards(Collections.emptyList());
        page.setRecords(Collections.singletonList(dto));

        when(accountCardService.getAccountWithCardByLastUpdated(any(AccountCardQueryDTO.class), eq(1), eq(10)))
                .thenReturn(page);

        // 调用 POST /account/query 接口
        mockMvc.perform(post("/account/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.total").value(1));

        verify(accountCardService).getAccountWithCardByLastUpdated(any(AccountCardQueryDTO.class), eq(1), eq(10));
    }
}
