package com.example.teacuploanpayments.controller;

import com.example.teacuploanpayments.entity.Payment;
import com.example.teacuploanpayments.entity.PaymentRepository;
import com.example.teacuploanpayments.model.CreatePaymentsForPurchaseRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @Autowired
    PaymentRepository repository;

    @PostMapping
    public ResponseEntity BulkCreatePaymentsForPurchase(
        @RequestBody CreatePaymentsForPurchaseRequest request) {

        boolean invalidRequest = false;
        String invalidRequestParam = "";
        if (request.purchaseId == 0) {
            invalidRequest = true;
            invalidRequestParam = "purchaseId";
        } else if (request.purchaseAmount == 0) {
            invalidRequest = true;
            invalidRequestParam = "purchaseAmount";
        } else if (request.installmentCount == 0) {
            invalidRequest = true;
            invalidRequestParam = "installmentCount";
        }

        if (invalidRequest) {
            return ResponseEntity
                .badRequest()
                .body(
                    Collections.singletonMap("message", String.format("Invalid %s of 0", invalidRequestParam))
                );
        }

        ArrayList<Payment> paymentList = new ArrayList<>();

        for (int i = 0; i < request.installmentCount; i++) {
            Payment payment = new Payment(
                request.purchaseId,
                (request.purchaseAmount / request.installmentCount),
                "payment_unpaid",
                Timestamp.from(Instant.now().plusSeconds(3600*30*(i+1))),
                null,
                i+1
            );

            paymentList.add(payment);
        }

        Iterable<Payment> createdPayments = repository.saveAll(paymentList);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayments);

    }
}
