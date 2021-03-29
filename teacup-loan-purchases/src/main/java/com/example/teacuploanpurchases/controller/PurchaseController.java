package com.example.teacuploanpurchases.controller;

import com.example.teacuploanpurchases.entity.Purchase;
import com.example.teacuploanpurchases.entity.PurchaseRepository;
import com.example.teacuploanpurchases.model.CreatePurchaseRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {
    @Value( "${account.service}" )
    private String accountServiceUrl;
    private final String LOAN_DISBURSED_STATUS = "loan_disbursed";

    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    PurchaseRepository repository;

    @PostMapping()
    public ResponseEntity createPurchase(@RequestBody CreatePurchaseRequest request) {
        // TODO: Add tests for this
        Long requestAmount = request.getAmount();

        Purchase purchase = new Purchase(
                request.getAccountId(),
                request.getDescription(),
                requestAmount,
                request.getInstallmentPeriodMonth(),
                LOAN_DISBURSED_STATUS
        );


        // @TODO: data will go out of sync if fail creating purchase after subtracting limit
        String subtractLimitUri = accountServiceUrl + "/api/accounts/{id}/limit-subtraction/{amount}";

        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("id", request.getAccountId());
        uriVariables.put("amount", requestAmount);

        try {
            restTemplate.exchange(
                subtractLimitUri, HttpMethod.PUT,
                null, new ParameterizedTypeReference<Map<String, Object>>() {},
                uriVariables
            );
        } catch (HttpClientErrorException e) {
            String accountResponse = e.getResponseBodyAsString();

            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> responseBody;

            try {
                responseBody = mapper.readValue(accountResponse, Map.class);
            } catch (JsonProcessingException e2) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            if (e.getRawStatusCode() == HttpStatus.BAD_REQUEST.value()) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap(
                                "message",
                                responseBody
                                        .getOrDefault("message", "Unable to create a purchase")
                        ));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(accountResponse);
            }
        }

        Purchase purchaseCreated = repository.save(purchase);

        return ResponseEntity.ok()
                .body(Collections.singletonMap("id", purchaseCreated.getId()));
    }
}
