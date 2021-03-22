package com.example.teacuploanaccounts.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest()
@AutoConfigureMockMvc
public class AccountControllerTests {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup () {
        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
        mockMvc = builder.build();
    }


    @Test
    public void testAccountControllerHandleCreateAccountRequest () throws Exception {
        String mockCreateAccountRequest = "{\"holderName\":\"Jane Doe\",\"phoneNumber\":\"62812345678916\"}";
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/api/accounts").contentType(MediaType.APPLICATION_JSON).content(mockCreateAccountRequest);
        this.mockMvc
                .perform(builder)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print())
        ;
    }
}
