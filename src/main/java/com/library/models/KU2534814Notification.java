package com.library.models;

import java.time.LocalDateTime;

public class KU2534814Notification {
    private int id;
    private int userId;
    private String notificationType; // Due Date, Overdue, Reservation Available, Fine, General
    private String message;
    private boolean isRead;
    private LocalDateTime sentDate;

    public KU2534814Notification(int userId, String notificationType, String message) {
        this.userId = userId;
        this.notificationType = notificationType;
        this.message = message;
        this.isRead = false;
        this.sentDate = LocalDateTime.now();
    }

    public KU2534814Notification(int id, int userId, String notificationType, String message, boolean isRead, LocalDateTime sentDate) {
        this.id = id;
        this.userId = userId;
        this.notificationType = notificationType;
        this.message = message;
        this.isRead = isRead;
        this.sentDate = sentDate;
    }

    public void markAsRead() {
        this.isRead = true;
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

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getSentDate() {
        return sentDate;
    }

    public void setSentDate(LocalDateTime sentDate) {
        this.sentDate = sentDate;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", userId=" + userId +
                ", notificationType='" + notificationType + '\'' +
                ", message='" + message + '\'' +
                ", isRead=" + isRead +
                ", sentDate=" + sentDate +
                '}';
    }
}
