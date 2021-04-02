package com.example.teacuploanpayments.controller;

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

@SpringBootTest()
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class PaymentControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testPaymentControllerHandleCreatePaymentsRequest() throws Exception {
        String mockCreatePaymentsRequest = "{\"purchaseId\":1,\"purchaseAmount\":100000, \"installmentCount\":6}";
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/api/payments").contentType(MediaType.APPLICATION_JSON).content(mockCreatePaymentsRequest);
        this.mockMvc
                .perform(builder)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print());
    }
}
