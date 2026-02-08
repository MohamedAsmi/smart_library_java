package com.library.decorators;

import com.library.interfaces.KU2534814BookComponentInterface;

public class KU2534814RecommendedBookDecorator extends KU2534814BookDecorator {
    public KU2534814RecommendedBookDecorator(KU2534814BookComponentInterface book) {
        super(book);
    }

    @Override
    public String getDescription() {
        return book.getDescription() + " [RECOMMENDED]";
    }

    @Override
    public double getPrice() {
        return book.getPrice() * 1.08; // 8% markup
    }
}
