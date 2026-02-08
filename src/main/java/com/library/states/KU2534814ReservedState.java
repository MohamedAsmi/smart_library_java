package com.library.states;

import com.library.interfaces.KU2534814BookStateInterface;
import com.library.models.KU2534814Book;
import com.library.models.KU2534814User;

import java.time.LocalDate;

public class KU2534814ReservedState implements KU2534814BookStateInterface {
    @Override
    public void borrow(KU2534814Book book, KU2534814User user) {
        if (book.getReservedBy() == user) {
            book.setBorrowedBy(user);
            book.setBorrowDate(LocalDate.now());
            book.setDueDate(LocalDate.now().plusDays(14));
            user.removeReservedBook(book);
            book.setReservedBy(null);
            book.setState(new KU2534814BorrowedState());
            user.addBorrowedBook(book);
            System.out.println("Reserved book '" + book.getTitle() + "' borrowed by " + user.getName());
        } else {
            System.out.println("Book is reserved by another user.");
        }
    }

    @Override
    public void returnBook(KU2534814Book book) {
        System.out.println("Book is not borrowed.");
    }

    @Override
    public void reserve(KU2534814Book book, KU2534814User user) {
        System.out.println("Book is already reserved.");
    }

    @Override
    public void cancelReservation(KU2534814Book book) {
        KU2534814User user = book.getReservedBy();
        user.removeReservedBook(book);
        book.setReservedBy(null);
        book.setState(new KU2534814AvailableState());
        System.out.println("Reservation cancelled for book '" + book.getTitle() + "'");
    }

    @Override
    public String getStateName() {
        return "Reserved";
    }
}
