package com.library.commands;

import com.library.interfaces.KU2534814CommandInterface;

import java.util.ArrayList;
import java.util.List;

public class KU2534814CommandHistory {
    private List<KU2534814CommandInterface> history;
    private int currentPosition;

    public KU2534814CommandHistory() {
        this.history = new ArrayList<>();
        this.currentPosition = -1;
    }

    public void execute(KU2534814CommandInterface command) {
        // Remove any commands after the current position
        while (history.size() > currentPosition + 1) {
            history.remove(history.size() - 1);
        }
        
        command.execute();
        history.add(command);
        currentPosition++;
    }

    public void undo() {
        if (currentPosition >= 0) {
            KU2534814CommandInterface command = history.get(currentPosition);
            command.undo();
            currentPosition--;
            System.out.println("Undone: " + command.getDescription());
        } else {
            System.out.println("No commands to undo.");
        }
    }

    public void redo() {
        if (currentPosition < history.size() - 1) {
            currentPosition++;
            KU2534814CommandInterface command = history.get(currentPosition);
            command.execute();
            System.out.println("Redone: " + command.getDescription());
        } else {
            System.out.println("No commands to redo.");
        }
    }

    public void showHistory() {
        System.out.println("\nCommand History:");
        for (int i = 0; i < history.size(); i++) {
            String marker = (i == currentPosition) ? " <-- Current" : "";
            System.out.println((i + 1) + ". " + history.get(i).getDescription() + marker);
        }
    }
}
