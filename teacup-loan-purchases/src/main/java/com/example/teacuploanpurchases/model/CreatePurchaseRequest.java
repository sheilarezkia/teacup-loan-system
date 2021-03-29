package com.example.teacuploanpurchases.model;

public class CreatePurchaseRequest {
    private int accountId;
    private String description;
    private Long amount;
    private Integer installmentPeriodMonth;

    public int getAccountId() {
        return accountId;
    }

    public String getDescription() {
        return description;
    }

    public Long getAmount() {
        return amount;
    }

    public Integer getInstallmentPeriodMonth() {
        return installmentPeriodMonth;
    }
}
