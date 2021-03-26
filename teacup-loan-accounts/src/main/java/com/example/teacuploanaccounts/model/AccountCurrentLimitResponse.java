package com.example.teacuploanaccounts.model;

public class AccountCurrentLimitResponse {
    int id;
    long currentLimit;

    public AccountCurrentLimitResponse(int id, long currentLimit) {
        this.id = id;
        this.currentLimit = currentLimit;
    }

    public int getId() {
        return id;
    }

    public long getCurrentLimit() {
        return currentLimit;
    }
}
