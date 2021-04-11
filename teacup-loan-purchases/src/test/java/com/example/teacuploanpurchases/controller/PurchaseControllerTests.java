package com.example.teacuploanpurchases.controller;

import com.example.teacuploanpurchases.entity.Purchase;
import com.example.teacuploanpurchases.entity.PurchaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class PurchaseControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Value( "${account.service}" )
    private String accountsServiceUrl;

    @Value( "${payment.service}" )
    private String paymentsServiceUrl;

    @MockBean
    private PurchaseRepository purchaseRepository;
    private MockRestServiceServer mockServer;

    @Autowired
    RestTemplate restTemplate;

    @BeforeEach
    public void setUp () {
        this.mockServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    public void tesUpdatePurchaseStatusWithValidRequest() throws Exception {
        Purchase mockPurchaseRecord = new Purchase(
                1,
                "mock purchase object",
                1000,
                12,
                "loan_disbursed"
        );
        Mockito.when(purchaseRepository.findById(1)).thenReturn(Optional.of(mockPurchaseRecord));

        String mockUpdatePurchaseStatusRequest = "{\"status\": \"loan_closed\"}";
        MockHttpServletRequestBuilder builder =
            MockMvcRequestBuilders.put("/api/purchases/1/status-updates")
                .contentType(MediaType.APPLICATION_JSON).content(mockUpdatePurchaseStatusRequest);

        this.mockMvc
                .perform(builder)
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void tesUpdatePurchaseStatusWithInvalidStatus() throws Exception {
        String mockUpdatePurchaseStatusRequest = "{\"status\": \"dummy_invalid_status\"}";
        MockHttpServletRequestBuilder builder =
            MockMvcRequestBuilders.put("/api/purchases/1/status-updates")
                .contentType(MediaType.APPLICATION_JSON).content(mockUpdatePurchaseStatusRequest);

        this.mockMvc
            .perform(builder)
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                .value("Invalid loan status"))
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void tesUpdatePurchaseStatusWithInvalidIdParam() throws Exception {
        String mockUpdatePurchaseStatusRequest = "{\"status\": \"loan_closed\"}";
        MockHttpServletRequestBuilder builder =
            MockMvcRequestBuilders.put("/api/purchases/invalid-id/status-updates")
                .contentType(MediaType.APPLICATION_JSON).content(mockUpdatePurchaseStatusRequest);

        this.mockMvc
            .perform(builder)
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.message")
                    .value("Invalid purchase id on request"))
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testMockPurchaseRecordNotFoundReturns404() throws Exception {
        Mockito.when(purchaseRepository.findById(1)).thenReturn(Optional.empty());

        String mockUpdatePurchaseStatusRequest = "{\"status\": \"loan_closed\"}";
        MockHttpServletRequestBuilder builder =
            MockMvcRequestBuilders.put("/api/purchases/1/status-updates")
                .contentType(MediaType.APPLICATION_JSON).content(mockUpdatePurchaseStatusRequest);

        this.mockMvc
            .perform(builder)
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.message")
                    .value("Unable to find a purchase record with the given id"))
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testControllerHandleCreatePurchaseRequestWithValidRequestedLoanAmount() throws Exception {
        String limitSubstractionUrl = accountsServiceUrl + "/1/limit-subtraction/5000";
        String createPaymentsUrl = paymentsServiceUrl + "/";

        this.mockServer.expect(requestTo(limitSubstractionUrl))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withStatus(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            );

        this.mockServer.expect(requestTo(createPaymentsUrl))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withStatus(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
            );

        Mockito.when(purchaseRepository.save(Mockito.any(Purchase.class)))
            .thenReturn(new Purchase());

        String mockCreatePurchaseRequest = "{" +
            "\"accountId\":1," +
            "\"description\":\"Test create purchase\"," +
            "\"amount\":5000," +
            "\"installmentPeriodMonth\":12" +
        "}";

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
            .post("/api/purchases")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mockCreatePurchaseRequest);

        this.mockMvc
            .perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk());
        this.mockServer.verify();
    }

    @Test
    public void testControllerHandleCreatePurchaseRequestWithInvalidRequestedLoanAmount() throws Exception {
        String mockCreatePurchaseRequest = "{" +
            "\"accountId\":1," +
            "\"description\":\"Test create purchase\"," +
            "\"amount\":15000," +
            "\"installmentPeriodMonth\":12" +
            "}";

        String limitSubstractionUrl = accountsServiceUrl + "/1/limit-subtraction/15000";
        String mockExpectedResponse = "{\"message\":\"Amount to subtract exceeds account's current limit\"}";

        this.mockServer.expect(requestTo(limitSubstractionUrl))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withBadRequest().body(mockExpectedResponse));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
            .post("/api/purchases")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mockCreatePurchaseRequest);

        this.mockMvc
            .perform(builder)
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.content().string(mockExpectedResponse))
            .andDo(MockMvcResultHandlers.print());
    }
}
