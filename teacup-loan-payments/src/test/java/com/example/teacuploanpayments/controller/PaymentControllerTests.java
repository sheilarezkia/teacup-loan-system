package com.example.teacuploanpayments.controller;

import com.example.teacuploanpayments.entity.Payment;
import com.example.teacuploanpayments.entity.PaymentRepository;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

@SpringBootTest()
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class PaymentControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentRepository paymentRepository;

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

    @Test
    public void testPaymentControllerHandleValidCompletePaymentRequest() throws Exception {
        Payment mockPayment = new Payment();
        mockPayment.setStatus("payment_unpaid");
        mockPayment.setBillAmount((long)16666);
        Mockito.when(paymentRepository.findById(1)).thenReturn(Optional.of(mockPayment));


        String mockCompletePaymentRequest = "{\"paymentId\": 1,\"paidAmount\": 16666}";
        MockHttpServletRequestBuilder builder =
            MockMvcRequestBuilders.put("/api/payments/completion")
                .contentType(MediaType.APPLICATION_JSON).content(mockCompletePaymentRequest);

        this.mockMvc
            .perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testPaymentControllerHandleDuplicatePaymentCompletionRequest() throws Exception {
        Payment mockPayment = new Payment();
        mockPayment.setStatus("payment_paid");
        mockPayment.setBillAmount((long)16666);
        Mockito.when(paymentRepository.findById(1)).thenReturn(Optional.of(mockPayment));


        String mockCompletePaymentRequest = "{\"paymentId\": 1,\"paidAmount\": 16666}";
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/payments/completion")
                        .contentType(MediaType.APPLICATION_JSON).content(mockCompletePaymentRequest);

        this.mockMvc
            .perform(builder)
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Payment is already paid"))
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testPaymentControllerHandleInvalidPaymentCompletionAmountRequest() throws Exception {
        Payment mockPayment = new Payment();
        mockPayment.setStatus("payment_unpaid");
        mockPayment.setBillAmount((long)1000);
        Mockito.when(paymentRepository.findById(1)).thenReturn(Optional.of(mockPayment));


        String mockCompletePaymentRequest = "{\"paymentId\": 1,\"paidAmount\": 16666}";
        MockHttpServletRequestBuilder builder =
            MockMvcRequestBuilders.put("/api/payments/completion")
                .contentType(MediaType.APPLICATION_JSON).content(mockCompletePaymentRequest);

        this.mockMvc
            .perform(builder)
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                .value("Amount paid doesn't match payment bill amount"))
            .andDo(MockMvcResultHandlers.print());
    }
}
