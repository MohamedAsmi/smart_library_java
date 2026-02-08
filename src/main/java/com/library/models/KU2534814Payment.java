package com.library.models;

import java.time.LocalDateTime;

public class KU2534814Payment {
    private int id;
    private int userId;
    private Integer fineId;
    private double amount;
    private String paymentMethod; // Cash, Card, Online, Bank Transfer
    private String transactionReference;
    private LocalDateTime paymentDate;
    private String status; // Pending, Completed, Failed, Refunded
    private String notes;

    public KU2534814Payment(int userId, double amount, String paymentMethod) {
        this.userId = userId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = LocalDateTime.now();
        this.status = "Completed";
        this.transactionReference = generateTransactionReference();
    }

    public KU2534814Payment(int id, int userId, Integer fineId, double amount, String paymentMethod, String status) {
        this.id = id;
        this.userId = userId;
        this.fineId = fineId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.paymentDate = LocalDateTime.now();
        this.transactionReference = generateTransactionReference();
    }

    private String generateTransactionReference() {
        return "TXN-" + System.currentTimeMillis() + "-" + userId;
    }

    public boolean isCompleted() {
        return "Completed".equals(status);
    }

    public boolean isFailed() {
        return "Failed".equals(status);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getFineId() {
        return fineId;
    }

    public void setFineId(Integer fineId) {
        this.fineId = fineId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", userId=" + userId +
                ", fineId=" + fineId +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", transactionReference='" + transactionReference + '\'' +
                ", paymentDate=" + paymentDate +
                ", status='" + status + '\'' +
                '}';
    }
}
