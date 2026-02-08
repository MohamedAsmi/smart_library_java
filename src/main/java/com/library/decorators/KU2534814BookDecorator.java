package com.library.decorators;

import com.library.interfaces.KU2534814BookComponentInterface;

public abstract class KU2534814BookDecorator implements KU2534814BookComponentInterface {
    protected KU2534814BookComponentInterface book;

    public KU2534814BookDecorator(KU2534814BookComponentInterface book) {
        this.book = book;
    }

    @Override
    public String getDescription() {
        return book.getDescription();
    }

    @Override
    public double getPrice() {
        return book.getPrice();
    }
}
