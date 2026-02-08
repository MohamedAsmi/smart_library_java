package com.library.models;

import java.time.LocalDate;

public class KU2534814BorrowTransaction {
    private int id;
    private int bookId;
    private int userId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status; // Active, Returned, Overdue
    private double fineAmount;

    public KU2534814BorrowTransaction(int id, int bookId, int userId, LocalDate borrowDate, LocalDate dueDate) {
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = "Active";
        this.fineAmount = 0.0;
    }

    // Check if transaction is overdue
    public boolean isOverdue() {
        if (returnDate != null) {
            return false; // Already returned
        }
        return LocalDate.now().isAfter(dueDate);
    }

    // Calculate days overdue
    public int getDaysOverdue() {
        if (!isOverdue()) {
            return 0;
        }
        LocalDate checkDate = returnDate != null ? returnDate : LocalDate.now();
        return (int) java.time.temporal.ChronoUnit.DAYS.between(dueDate, checkDate);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public int getTransactionId() {
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

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
        if (returnDate != null) {
            this.status = "Returned";
        }
    }

    public String getStatus() {
        // Update status based on current state
        if (isOverdue() && returnDate == null) {
            return "Overdue";
        }
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }

    @Override
    public String toString() {
        return "BorrowTransaction{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", userId=" + userId +
                ", borrowDate=" + borrowDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                ", status='" + getStatus() + '\'' +
                ", fineAmount=" + fineAmount +
                '}';
    }
}
