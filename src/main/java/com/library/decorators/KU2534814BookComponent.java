package com.library.decorators;

import com.library.interfaces.KU2534814BookComponentInterface;

public abstract class KU2534814BookComponent implements KU2534814BookComponentInterface {
    protected String title;
    protected String author;
    protected double basePrice;

    public KU2534814BookComponent(String title, String author, double basePrice) {
        this.title = title;
        this.author = author;
        this.basePrice = basePrice;
    }

    @Override
    public String getDescription() {
        return title + " by " + author;
    }

    @Override
    public double getPrice() {
        return basePrice;
    }
}
