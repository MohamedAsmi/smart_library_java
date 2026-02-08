package com.library.models;

import com.library.interfaces.KU2534814BookStateInterface;
import com.library.states.KU2534814AvailableState;

import java.time.LocalDate;

public class KU2534814Book {
    private int id;
    private String isbn;
    private String title;
    private String author;
    private double price;
    private KU2534814BookStateInterface state;
    private KU2534814User borrowedBy;
    private KU2534814User reservedBy;
    private LocalDate borrowDate;
    private LocalDate dueDate;

    public KU2534814Book(int id, String isbn, String title, String author, double price) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.price = price;
        this.state = new KU2534814AvailableState();
    }

    // State methods
    public void borrow(KU2534814User user) {
        state.borrow(this, user);
    }

    public void returnBook() {
        state.returnBook(this);
    }

    public void reserve(KU2534814User user) {
        state.reserve(this, user);
    }

    public void cancelReservation() {
        state.cancelReservation(this);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public KU2534814BookStateInterface getState() {
        return state;
    }

    public void setState(KU2534814BookStateInterface state) {
        this.state = state;
    }

    public KU2534814User getBorrowedBy() {
        return borrowedBy;
    }

    public void setBorrowedBy(KU2534814User borrowedBy) {
        this.borrowedBy = borrowedBy;
    }

    public KU2534814User getReservedBy() {
        return reservedBy;
    }

    public void setReservedBy(KU2534814User reservedBy) {
        this.reservedBy = reservedBy;
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

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", price=" + price +
                ", state=" + state.getStateName() +
                '}';
    }
}
