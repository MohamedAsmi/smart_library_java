package com.library.states;

import com.library.interfaces.KU2534814BookStateInterface;
import com.library.models.KU2534814Book;
import com.library.models.KU2534814User;

public class KU2534814BorrowedState implements KU2534814BookStateInterface {
    @Override
    public void borrow(KU2534814Book book, KU2534814User user) {
        System.out.println("Book is already borrowed.");
    }

    @Override
    public void returnBook(KU2534814Book book) {
        KU2534814User user = book.getBorrowedBy();
        user.removeBorrowedBook(book);
        book.setBorrowedBy(null);
        book.setBorrowDate(null);
        book.setDueDate(null);
        
        // Check if book was reserved
        if (book.getReservedBy() != null) {
            book.setState(new KU2534814ReservedState());
        } else {
            book.setState(new KU2534814AvailableState());
        }
        System.out.println("Book '" + book.getTitle() + "' returned.");
    }

    @Override
    public void reserve(KU2534814Book book, KU2534814User user) {
        if (book.getReservedBy() == null) {
            book.setReservedBy(user);
            user.addReservedBook(book);
            System.out.println("Book '" + book.getTitle() + "' reserved by " + user.getName() + " (currently borrowed)");
        } else {
            System.out.println("Book is already reserved.");
        }
    }

    @Override
    public void cancelReservation(KU2534814Book book) {
        if (book.getReservedBy() != null) {
            KU2534814User user = book.getReservedBy();
            user.removeReservedBook(book);
            book.setReservedBy(null);
            System.out.println("Reservation cancelled for book '" + book.getTitle() + "'");
        } else {
            System.out.println("Book is not reserved.");
        }
    }

    @Override
    public String getStateName() {
        return "Borrowed";
    }
}
