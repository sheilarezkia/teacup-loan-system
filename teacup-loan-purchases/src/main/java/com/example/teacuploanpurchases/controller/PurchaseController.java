package com.example.teacuploanpurchases.controller;

import com.example.teacuploanpurchases.entity.Purchase;
import com.example.teacuploanpurchases.entity.PurchaseRepository;
import com.example.teacuploanpurchases.model.CreatePurchaseRequest;
import com.example.teacuploanpurchases.model.UpdatePurchaseStatusRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {
    @Value( "${account.service}" )
    private String accountsServiceUrl;

    @Value( "${payment.service}" )
    private String paymentsServiceUrl;

    private final String LOAN_DISBURSED_STATUS = "loan_disbursed";
    private final String LOAN_CLOSED_STATUS = "loan_closed";
    private final String LOAN_PENALTY_COLLECTION_STATUS = "loan_penalty_collection";

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


        // @TODO: data will go out of sync if failing to create purchase after subtracting limit
        // -- or when failing to create payment records after creating the purchase record

        String subtractLimitUri = accountsServiceUrl + "/{id}/limit-subtraction/{amount}";

        Map<String, Object> subtractLimitParam = new HashMap<>();
        subtractLimitParam.put("id", request.getAccountId());
        subtractLimitParam.put("amount", requestAmount);

        try {
            restTemplate.exchange(
                subtractLimitUri, HttpMethod.PUT,
                null, new ParameterizedTypeReference<Map<String, Object>>() {},
                subtractLimitParam
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
                            responseBody.getOrDefault("message", "Unable to create a purchase")
                        ));
            } else {

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(accountResponse);
            }
        }

        Purchase purchaseCreated = repository.save(purchase);

        Map<String, Object> createPaymentsRequest = new HashMap<>();
        createPaymentsRequest.put("purchaseId", purchaseCreated.getId());
        createPaymentsRequest.put("purchaseAmount", requestAmount);
        createPaymentsRequest.put("installmentCount", request.getInstallmentPeriodMonth());
        String createPaymentsUri = paymentsServiceUrl + "/";

        ResponseEntity<Map> paymentsResponse = restTemplate
                .postForEntity(createPaymentsUri, createPaymentsRequest, Map.class);

        if (paymentsResponse.getStatusCode() != HttpStatus.CREATED) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(paymentsResponse.getBody());
        }

        return ResponseEntity.ok()
                .body(Collections.singletonMap("id", purchaseCreated.getId()));
    }

    @PutMapping("/{id}/status-updates")
    public ResponseEntity updatePurchaseStatus(
            @PathVariable String id,
            @RequestBody UpdatePurchaseStatusRequest request) {
        String status = request.getStatus();
        System.out.println("status on request:" + status);

        if (!(status.equals(LOAN_CLOSED_STATUS) || status.equals(LOAN_PENALTY_COLLECTION_STATUS))) {
            return ResponseEntity.badRequest().body(
                    Collections.singletonMap("message", "Invalid loan status")
            );
        }

        int purchaseId;
        try {
            purchaseId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(
                    Collections.singletonMap("message", "Invalid purchase id on request")
            );
        }

        Optional<Purchase> queryResult = repository.findById(purchaseId);
        if (!queryResult.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Collections.singletonMap("message", "Unable to find a purchase record with the given id")
            );
        }

        Purchase purchase = queryResult.get();
        purchase.setStatus(request.getStatus());
        repository.save(purchase);

        return ResponseEntity.noContent().build();
    }
}