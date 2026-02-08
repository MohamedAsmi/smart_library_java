package com.library.models;

import java.time.LocalDateTime;

public class KU2534814Fine {
    private int id;
    private int userId;
    private Integer borrowTransactionId;
    private double fineAmount;
    private int daysOverdue;
    private double fineRate;
    private String status; // Pending, Paid, Waived
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private LocalDateTime createdAt;

    // Constructor for creating fine from database (with ID)
    public KU2534814Fine(int id, int userId, double fineAmount, int daysOverdue, double fineRate) {
        this.id = id;
        this.userId = userId;
        this.fineAmount = fineAmount;
        this.daysOverdue = daysOverdue;
        this.fineRate = fineRate;
        this.status = "Pending";
        this.createdAt = LocalDateTime.now();
    }

    // Constructor for creating new fine (without ID, with transaction reference)
    public KU2534814Fine(int userId, Integer borrowTransactionId, double fineAmount, int daysOverdue, double fineRate) {
        this.userId = userId;
        this.borrowTransactionId = borrowTransactionId;
        this.fineAmount = fineAmount;
        this.daysOverdue = daysOverdue;
        this.fineRate = fineRate;
        this.status = "Pending";
        this.createdAt = LocalDateTime.now();
    }

    public boolean isPaid() {
        return "Paid".equals(status);
    }

    public boolean isPending() {
        return "Pending".equals(status);
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

    public Integer getBorrowTransactionId() {
        return borrowTransactionId;
    }

    public void setBorrowTransactionId(Integer borrowTransactionId) {
        this.borrowTransactionId = borrowTransactionId;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }

    public int getDaysOverdue() {
        return daysOverdue;
    }

    public void setDaysOverdue(int daysOverdue) {
        this.daysOverdue = daysOverdue;
    }

    public double getFineRate() {
        return fineRate;
    }

    public void setFineRate(double fineRate) {
        this.fineRate = fineRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Fine{" +
                "id=" + id +
                ", userId=" + userId +
                ", fineAmount=" + fineAmount +
                ", daysOverdue=" + daysOverdue +
                ", fineRate=" + fineRate +
                ", status='" + status + '\'' +
                ", paymentDate=" + paymentDate +
                '}';
    }
}
