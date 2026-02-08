package com.library.strategies;

import com.library.interfaces.KU2534814FineStrategyInterface;

public class KU2534814GuestFineStrategy implements KU2534814FineStrategyInterface {
    private static final double FINE_PER_DAY = 2.0;

    @Override
    public double calculateFine(int daysOverdue) {
        return daysOverdue * FINE_PER_DAY;
    }

    @Override
    public String getUserType() {
        return "Guest";
    }
}
