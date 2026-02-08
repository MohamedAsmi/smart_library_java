package com.library.models;

import java.time.LocalDateTime;

public class KU2534814Reservation {
    private int id;
    private int bookId;
    private int userId;
    private LocalDateTime reservationDate;
    private String status; // Active, Fulfilled, Cancelled, Expired
    private boolean notificationSent;
    private LocalDateTime expiryDate;

    public KU2534814Reservation(int id, int bookId, int userId) {
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.reservationDate = LocalDateTime.now();
        this.status = "Active";
        this.notificationSent = false;
        // Reservation expires after 48 hours
        this.expiryDate = LocalDateTime.now().plusHours(48);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate) && "Active".equals(status);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isNotificationSent() {
        return notificationSent;
    }

    public void setNotificationSent(boolean notificationSent) {
        this.notificationSent = notificationSent;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", userId=" + userId +
                ", reservationDate=" + reservationDate +
                ", status='" + status + '\'' +
                ", notificationSent=" + notificationSent +
                ", expiryDate=" + expiryDate +
                '}';
    }
}
