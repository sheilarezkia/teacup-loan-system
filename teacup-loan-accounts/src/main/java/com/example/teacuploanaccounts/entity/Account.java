package com.example.teacuploanaccounts.entity;


import javax.persistence.*;

@Entity
@Table(name="accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    String holderName;

    @Column
    String phoneNumber;

    @Column
    Long currentLimit;

    @Column
    Long maxLimit;

    public Account(String holderName, String phoneNumber, long currentLimit, long maxLimit) {
        this.holderName = holderName;
        this.phoneNumber = phoneNumber;
        this.currentLimit = currentLimit;
        this.maxLimit = maxLimit;
    }

    public Account() {

    }

    public int getId() {
        return id;
    }

    public long getCurrentLimit() {
        return this.currentLimit;
    }

    public long getMaxLimit() {
        return this.maxLimit;
    }
}
