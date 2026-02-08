package com.library.interfaces;

public interface KU2534814SubjectInterface {
    void attach(KU2534814ObserverInterface observer);
    void detach(KU2534814ObserverInterface observer);
    void notifyObservers(String message);
}
