package com.library.decorators;

import com.library.interfaces.KU2534814BookComponentInterface;

public class KU2534814SpecialEditionBookDecorator extends KU2534814BookDecorator {
    public KU2534814SpecialEditionBookDecorator(KU2534814BookComponentInterface book) {
        super(book);
    }

    @Override
    public String getDescription() {
        return book.getDescription() + " [SPECIAL EDITION]";
    }

    @Override
    public double getPrice() {
        return book.getPrice() * 1.30; // 30% markup
    }
}
