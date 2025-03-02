package com.example.emspdemo.presentation.controller;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.emspdemo.EMspDemoApplication;
import com.example.emspdemo.application.command.ChangeAccountStatusCommand;
import com.example.emspdemo.application.command.CreateAccountCommand;
import com.example.emspdemo.application.query.AccountQuery;
import com.example.emspdemo.application.service.AccountService;
import com.example.emspdemo.domain.Account;
import com.example.emspdemo.domain.enums.AccountStatus;
import com.example.emspdemo.presentation.dto.AccountDTO;
import com.example.emspdemo.presentation.dto.common.BaseRequest;
import com.example.emspdemo.presentation.dto.common.PageRequest;
import com.example.emspdemo.presentation.dto.common.PageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AccountController.class,
        excludeAutoConfiguration = {MybatisPlusAutoConfiguration.class})
// 显式指定只加载 AccountController 与 MockConfig，从而不扫描主应用 EMspDemoApplication
@ContextConfiguration(classes = {AccountController.class, AccountControllerTest.TestConfig.class})
public class AccountControllerTest {

    // 使用字段注入避免构造器参数解析问题
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 手动通过 TestConfig 注入 AccountService 模拟 Bean
    @Autowired
    private AccountService accountService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public AccountService accountService() {
            return Mockito.mock(AccountService.class);
        }
    }

    @Test
    void testCreateAccount() throws Exception {
        CreateAccountCommand command = new CreateAccountCommand();
        command.setEmail("test@example.com");
        command.setEditor("admin");

        BaseRequest<CreateAccountCommand> request = new BaseRequest<>();
        request.setRequestNo("REQ-001");
        request.setRequestData(command);

        Account account = new Account();
        account.setId(1L);
        account.setEmail("test@example.com");
        account.setEditor("admin");
        account.setStatus(AccountStatus.CREATED);
        account.setLastUpdated(new Date());

        Mockito.when(accountService.createAccount(Mockito.any(CreateAccountCommand.class)))
                .thenReturn(account);

        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.email", is("test@example.com")))
                .andExpect(jsonPath("$.data.editor", is("admin")));
    }

    @Test
    void testChangeAccountStatus() throws Exception {
        ChangeAccountStatusCommand command = new ChangeAccountStatusCommand();
        command.setStatus(AccountStatus.ACTIVATED);
        command.setEditor("operator");

        BaseRequest<ChangeAccountStatusCommand> request = new BaseRequest<>();
        request.setRequestNo("REQ-002");
        request.setRequestData(command);

        Account account = new Account();
        account.setId(1L);
        account.setEmail("test@example.com");
        account.setEditor("operator");
        account.setStatus(AccountStatus.ACTIVATED);
        account.setLastUpdated(new Date());

        Mockito.when(accountService.changeAccountStatus(Mockito.eq(1L), Mockito.any(ChangeAccountStatusCommand.class)))
                .thenReturn(account);

        mockMvc.perform(patch("/account/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.status", is("ACTIVATED")))
                .andExpect(jsonPath("$.data.editor", is("operator")));
    }

    @Test
    void testQueryAccounts() throws Exception {
        AccountQuery query = new AccountQuery();
        PageRequest<AccountQuery> request = new PageRequest<>();
        request.setRequestNo("REQ-003");
        request.setRequestData(query);
        request.setPageNo(0);
        request.setPageSize(10);

        Page<Account> page = new Page<>(0, 10, 1);
        Account account = new Account();
        account.setId(1L);
        account.setEmail("test@example.com");
        account.setEditor("admin");
        account.setStatus(AccountStatus.CREATED);
        account.setLastUpdated(new Date());
        page.setRecords(Collections.singletonList(account));

        Mockito.when(accountService.queryAccounts(Mockito.any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(post("/account/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records[0].id", is(1)))
                .andExpect(jsonPath("$.data.records[0].email", is("test@example.com")));
    }
}
