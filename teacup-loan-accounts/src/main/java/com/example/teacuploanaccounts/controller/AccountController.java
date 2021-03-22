package com.example.teacuploanaccounts.controller;
import com.example.teacuploanaccounts.entity.Account;
import com.example.teacuploanaccounts.entity.AccountRepository;
import com.example.teacuploanaccounts.model.CreateAccountRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            response = new ResponseEntity("Received illegal argument for account creation", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }
}