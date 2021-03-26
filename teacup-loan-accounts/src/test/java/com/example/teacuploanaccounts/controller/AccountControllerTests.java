package com.example.teacuploanaccounts.controller;

import com.example.teacuploanaccounts.entity.Account;
import com.example.teacuploanaccounts.entity.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@RunWith(SpringRunner.class)
@SpringBootTest()
@AutoConfigureMockMvc
public class AccountControllerTests {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountRepository accountRepository;

    @BeforeEach
    public void setup () {
        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
        mockMvc = builder.build();
    }


    @Test
    public void testAccountControllerHandleCreateAccountRequest() throws Exception {
        String mockCreateAccountRequest = "{\"holderName\":\"Jane Doe\",\"phoneNumber\":\"62812345678916\"}";
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/api/accounts").contentType(MediaType.APPLICATION_JSON).content(mockCreateAccountRequest);
        this.mockMvc
                .perform(builder)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testAccountControllerHandleGetAccountInfoRequest() throws Exception {
        Account mockAccount = new Account("John Doe", "62000001", 500000, 5000000);

        Mockito.when(accountRepository.findById(1)).thenReturn(Optional.of(mockAccount));

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/api/accounts/1/current-limit");

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json("{\n" +
                        "  \"id\": 1,\n" +
                        "  \"currentLimit\": 500000\n" +
                        "}"));
    }
}
