package com.example.teacuploanaccounts.controller;
import com.example.teacuploanaccounts.entity.Account;
import com.example.teacuploanaccounts.entity.AccountRepository;
import com.example.teacuploanaccounts.model.AccountCurrentLimitResponse;
import com.example.teacuploanaccounts.model.CreateAccountRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
class AccountController {
    private final long MAX_LIMIT = 50000000;
    @Autowired
    AccountRepository repository;

    @PostMapping()
    @ResponseBody
    public ResponseEntity<String> createAccount(@RequestBody CreateAccountRequest request) {
        ResponseEntity<String> response;
        Account account = new Account(
                request.getHolderName(),
                request.getPhoneNumber(),
                MAX_LIMIT,
                MAX_LIMIT
        );

        Account savedAccount;
        try {
            savedAccount = repository.save(account);
            response = new ResponseEntity(savedAccount, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            response = new ResponseEntity<String>("Received illegal argument for account creation", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }

    @GetMapping("/{id}/current-limit")
    public ResponseEntity getAccountCurrentLimit(@PathVariable String id) {
        Integer parsedId = Integer.parseInt(id);
        Optional<Account> result = repository.findById(parsedId);

        if (!result.isPresent()) {
            return new ResponseEntity<String>("Cannot find an account of id " + id, HttpStatus.NOT_FOUND);
        }

        AccountCurrentLimitResponse responseObject = new AccountCurrentLimitResponse(parsedId, result.get().getCurrentLimit());
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @PutMapping("/{id}/limit-subtraction/{amount}")
    public ResponseEntity subtractLimit(@PathVariable String id, @PathVariable  String amount) {
        Long amountToSubtract;
        try {
            amountToSubtract = Long.parseLong(amount);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("message", "Invalid amount given on param"));
        }

        Integer parsedId;
        try {
            parsedId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("message", "Invalid id given on param"));
        }

        Optional<Account> result = repository.findById(parsedId);

        if (!result.isPresent()) {
            return new ResponseEntity<>(
                    Collections.singletonMap("message", "Cannot find an account of id " + id),
                    HttpStatus.NOT_FOUND
            );
        }

        Account account = result.get();
        Long currentLimit = account.getCurrentLimit();

        if (currentLimit < amountToSubtract) {
            return ResponseEntity.badRequest()
                    .body(
                        Collections.singletonMap("message", "Amount to subtract exceeds account's current limit")
                    );
        } else {
            account.setCurrentLimit(currentLimit - amountToSubtract);
        }

        repository.save(account);

        return ResponseEntity.ok().body(account);
    }
}