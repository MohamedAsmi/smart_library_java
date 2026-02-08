package com.library.builders;

import com.library.models.KU2534814Book;

public class KU2534814BookBuilder {
    private int id;
    private String isbn;
    private String title;
    private String author;
    private double price;

    public KU2534814BookBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public KU2534814BookBuilder setIsbn(String isbn) {
        this.isbn = isbn;
        return this;
    }

    public KU2534814BookBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public KU2534814BookBuilder setAuthor(String author) {
        this.author = author;
        return this;
    }

    public KU2534814BookBuilder setPrice(double price) {
        this.price = price;
        return this;
    }

    public KU2534814Book build() {
        return new KU2534814Book(id, isbn, title, author, price);
    }
}
