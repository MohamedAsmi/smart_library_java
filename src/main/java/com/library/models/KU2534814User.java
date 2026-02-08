package com.library.models;

import com.library.interfaces.KU2534814FineStrategyInterface;
import com.library.interfaces.KU2534814ObserverInterface;

import java.util.ArrayList;
import java.util.List;

public class KU2534814User implements KU2534814ObserverInterface {
    private int id;
    private String name;
    private String email;
    private String userType; // "Student", "Faculty", "Guest"
    private KU2534814FineStrategyInterface fineStrategy;
    private List<KU2534814Book> borrowedBooks;
    private List<KU2534814Book> reservedBooks;

    public KU2534814User(int id, String name, String email, String userType, KU2534814FineStrategyInterface fineStrategy) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.userType = userType;
        this.fineStrategy = fineStrategy;
        this.borrowedBooks = new ArrayList<>();
        this.reservedBooks = new ArrayList<>();
    }

    public double calculateFine(int daysOverdue) {
        return fineStrategy.calculateFine(daysOverdue);
    }

    public void addBorrowedBook(KU2534814Book book) {
        borrowedBooks.add(book);
    }

    public void removeBorrowedBook(KU2534814Book book) {
        borrowedBooks.remove(book);
    }

    public void addReservedBook(KU2534814Book book) {
        reservedBooks.add(book);
    }

    public void removeReservedBook(KU2534814Book book) {
        reservedBooks.remove(book);
    }

    @Override
    public void update(String message) {
        System.out.println("Notification for " + name + ": " + message);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public KU2534814FineStrategyInterface getFineStrategy() {
        return fineStrategy;
    }

    public void setFineStrategy(KU2534814FineStrategyInterface fineStrategy) {
        this.fineStrategy = fineStrategy;
    }

    public List<KU2534814Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void setBorrowedBooks(List<KU2534814Book> borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }

    public List<KU2534814Book> getReservedBooks() {
        return reservedBooks;
    }

    public void setReservedBooks(List<KU2534814Book> reservedBooks) {
        this.reservedBooks = reservedBooks;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", userType='" + userType + '\'' +
                ", borrowedBooks=" + borrowedBooks.size() +
                ", reservedBooks=" + reservedBooks.size() +
                '}';
    }
}
