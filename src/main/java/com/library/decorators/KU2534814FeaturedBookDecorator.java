package com.library.decorators;

import com.library.interfaces.KU2534814BookComponentInterface;

public class KU2534814FeaturedBookDecorator extends KU2534814BookDecorator {
    public KU2534814FeaturedBookDecorator(KU2534814BookComponentInterface book) {
        super(book);
    }

    @Override
    public String getDescription() {
        return book.getDescription() + " [FEATURED]";
    }

    @Override
    public double getPrice() {
        return book.getPrice() * 1.15; // 15% markup
    }
}
