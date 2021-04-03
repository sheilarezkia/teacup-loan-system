package com.example.teacuploanpayments.entity;
import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name="payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Integer purchaseId;

    @Column
    private Long billAmount;

    @Column
    private String status;

    @Column
    private Timestamp dueDate;

    @Column
    private Timestamp paidAt;

    @Column
    private Integer paymentNumber;

    public Payment() {
    }

    public Payment(
            Integer purchaseId,
            Long billAmount,
            String status,
            Timestamp dueDate,
            Timestamp paidAt,
            Integer paymentNumber) {
        this.purchaseId = purchaseId;
        this.billAmount = billAmount;
        this.status = status;
        this.dueDate = dueDate;
        this.paidAt = paidAt;
        this.paymentNumber = paymentNumber;
    }

    public Integer getId() {
        return id;
    }

    public Integer getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Integer purchaseId) {
        this.purchaseId = purchaseId;
    }

    public Long getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(Long billAmount) {
        this.billAmount = billAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getDueDate() {
        return dueDate;
    }

    public void setDueDate(Timestamp dueDate) {
        this.dueDate = dueDate;
    }

    public Timestamp getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Timestamp paidAt) {
        this.paidAt = paidAt;
    }

    public Integer getPaymentNumber() {
        return paymentNumber;
    }

    public void setPaymentNumber(Integer paymentNumber) {
        this.paymentNumber = paymentNumber;
    }
}
