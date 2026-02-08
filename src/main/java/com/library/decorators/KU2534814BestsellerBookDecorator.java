package com.library.decorators;

import com.library.interfaces.KU2534814BookComponentInterface;

public class KU2534814BestsellerBookDecorator extends KU2534814BookDecorator {
    public KU2534814BestsellerBookDecorator(KU2534814BookComponentInterface book) {
        super(book);
    }

    @Override
    public String getDescription() {
        return book.getDescription() + " [BESTSELLER]";
    }

    @Override
    public double getPrice() {
        return book.getPrice() * 1.20; // 20% markup
    }
}
