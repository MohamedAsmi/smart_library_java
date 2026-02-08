package com.library.commands;

import com.library.interfaces.KU2534814CommandInterface;
import com.library.models.KU2534814Book;

public class KU2534814CancelReservationCommand implements KU2534814CommandInterface {
    private KU2534814Book book;
    private boolean executed;

    public KU2534814CancelReservationCommand(KU2534814Book book) {
        this.book = book;
        this.executed = false;
    }

    @Override
    public void execute() {
        book.cancelReservation();
        executed = true;
    }

    @Override
    public void undo() {
        if (executed) {
            System.out.println("Cannot undo cancel reservation command.");
            executed = false;
        }
    }

    @Override
    public String getDescription() {
        return "Cancel reservation for book: " + book.getTitle();
    }
}
