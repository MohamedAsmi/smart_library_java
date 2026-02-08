package com.library.states;

import com.library.interfaces.KU2534814BookStateInterface;
import com.library.models.KU2534814Book;
import com.library.models.KU2534814User;

import java.time.LocalDate;

public class KU2534814AvailableState implements KU2534814BookStateInterface {
    @Override
    public void borrow(KU2534814Book book, KU2534814User user) {
        book.setBorrowedBy(user);
        book.setBorrowDate(LocalDate.now());
        book.setDueDate(LocalDate.now().plusDays(14));
        book.setState(new KU2534814BorrowedState());
        user.addBorrowedBook(book);
        System.out.println("Book '" + book.getTitle() + "' borrowed by " + user.getName());
    }

    @Override
    public void returnBook(KU2534814Book book) {
        System.out.println("Book is not borrowed.");
    }

    @Override
    public void reserve(KU2534814Book book, KU2534814User user) {
        book.setReservedBy(user);
        book.setState(new KU2534814ReservedState());
        user.addReservedBook(book);
        System.out.println("Book '" + book.getTitle() + "' reserved by " + user.getName());
    }

    @Override
    public void cancelReservation(KU2534814Book book) {
        System.out.println("Book is not reserved.");
    }

    @Override
    public String getStateName() {
        return "Available";
    }
}
