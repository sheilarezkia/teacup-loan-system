package com.example.teacuploanpurchases.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name="purchases")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private int accountId;

    @Column
    private String description;

    @Column
    private long amount;

    @Column
    private int installmentPeriodMonth;

    @Column
    private String status;

    @Column
    private Timestamp createdAt;

    public Purchase(
            int accountId,
            String description,
            long amount,
            int installmentPeriodMonth,
            String status
    ) {
        this.accountId = accountId;
        this.description = description;
        this.amount = amount;
        this.installmentPeriodMonth = installmentPeriodMonth;
        this.status = status;
    }

    @PrePersist
    public void prePersist(){
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public int getId() {
        return id;
    }
}
