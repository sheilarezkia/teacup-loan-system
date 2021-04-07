package com.example.teacuploanpurchases.controller;

import com.example.teacuploanpurchases.entity.Purchase;
import com.example.teacuploanpurchases.entity.PurchaseRepository;
import com.example.teacuploanpurchases.model.AccountServiceGetInfoResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@SpringBootTest()
@AutoConfigureMockMvc
public class PurchaseControllerTests {

    private MockMvc mockMvc;

    @InjectMocks
    private PurchaseController purchaseController;
    @MockBean
    RestTemplate restTemplate;

    @MockBean
    private PurchaseRepository purchaseRepository;

    @Before
    public void setUp () {
        purchaseController = new PurchaseController();
        mockMvc = MockMvcBuilders.standaloneSetup(purchaseController).build();
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
        Mockito.when(restTemplate.exchange("http://localhost:8080/api/accounts/1/limit-subtraction/5000", HttpMethod.PUT, null, new ParameterizedTypeReference<Map<String, Object>>() {},
                any(String.class)))
                .thenReturn(ResponseEntity.ok(null));

        String mockCreatePurchaseRequest = "{" +
                "\"accountId\":1," +
                "\"description\":\"Test create purchase\"," +
                "\"amount\":5000," +
                "\"installmentPeriodMonth\":12" +
                "}";

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post("http://localhost:8082/api/purchases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mockCreatePurchaseRequest);

        this.mockMvc
                .perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testControllerHandleCreatePurchaseRequestWithInvalidRequestedLoanAmount() throws Exception {
        String mockCreatePurchaseRequest = "{" +
                "\"accountId\":1," +
                "\"description\":\"Test create purchase\"," +
                "\"amount\":15000," +
                "\"installmentPeriodMonth\":12" +
                "}";

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post("http://localhost:8082/api/purchases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mockCreatePurchaseRequest);

        AccountServiceGetInfoResponse getAccountInfoMock = new AccountServiceGetInfoResponse();
        getAccountInfoMock.currentLimit = 10000;
        getAccountInfoMock.accountId = 1;
        Mockito.when(restTemplate
                .getForEntity("http://localhost:8080/api/accounts/1", AccountServiceGetInfoResponse.class))
                .thenReturn(new ResponseEntity(getAccountInfoMock, HttpStatus.OK));

        this.mockMvc
                .perform(builder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(
                        MockMvcResultMatchers.content()
                                .string("Can't create a purchase of amount 15000")
                )
                .andDo(MockMvcResultHandlers.print());
    }
}
