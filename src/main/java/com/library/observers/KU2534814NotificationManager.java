package com.library.observers;

import com.library.interfaces.KU2534814ObserverInterface;
import com.library.interfaces.KU2534814SubjectInterface;

import java.util.ArrayList;
import java.util.List;

public class KU2534814NotificationManager implements KU2534814SubjectInterface {
    private List<KU2534814ObserverInterface> observers;

    public KU2534814NotificationManager() {
        this.observers = new ArrayList<>();
    }

    @Override
    public void attach(KU2534814ObserverInterface observer) {
        observers.add(observer);
    }

    @Override
    public void detach(KU2534814ObserverInterface observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (KU2534814ObserverInterface observer : observers) {
            observer.update(message);
        }
    }

    public void sendNotification(String message) {
        System.out.println("\n--- Broadcasting Notification ---");
        notifyObservers(message);
        System.out.println("--- End of Notification ---\n");
    }
}
