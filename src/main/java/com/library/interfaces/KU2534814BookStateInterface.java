package com.library.interfaces;

import com.library.models.KU2534814Book;
import com.library.models.KU2534814User;

public interface KU2534814BookStateInterface {
    void borrow(KU2534814Book book, KU2534814User user);
    void returnBook(KU2534814Book book);
    void reserve(KU2534814Book book, KU2534814User user);
    void cancelReservation(KU2534814Book book);
    String getStateName();
}
