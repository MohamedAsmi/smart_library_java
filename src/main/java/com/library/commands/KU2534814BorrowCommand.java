package com.library.commands;

import com.library.interfaces.KU2534814CommandInterface;
import com.library.models.KU2534814Book;
import com.library.models.KU2534814User;

public class KU2534814BorrowCommand implements KU2534814CommandInterface {
    private KU2534814Book book;
    private KU2534814User user;
    private boolean executed;

    public KU2534814BorrowCommand(KU2534814Book book, KU2534814User user) {
        this.book = book;
        this.user = user;
        this.executed = false;
    }

    @Override
    public void execute() {
        book.borrow(user);
        executed = true;
    }

    @Override
    public void undo() {
        if (executed) {
            book.returnBook();
            executed = false;
        }
    }

    @Override
    public String getDescription() {
        return "Borrow book: " + book.getTitle() + " by " + user.getName();
    }
}
