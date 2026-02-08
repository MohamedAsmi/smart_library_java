package com.library.decorators;

import com.library.interfaces.KU2534814BookComponentInterface;

public class KU2534814NewArrivalBookDecorator extends KU2534814BookDecorator {
    public KU2534814NewArrivalBookDecorator(KU2534814BookComponentInterface book) {
        super(book);
    }

    @Override
    public String getDescription() {
        return book.getDescription() + " [NEW ARRIVAL]";
    }

    @Override
    public double getPrice() {
        return book.getPrice() * 1.10; // 10% markup
    }
}
