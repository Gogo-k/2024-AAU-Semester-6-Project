package org.dslul.openboard.interfaces;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.dslul.openboard.interfaces.Subscriber;

// This class is responsible of notifying its observers when the user makes text
public abstract class Publisher {
   private List<Subscriber> subscribers = new ArrayList<>();


    public void addSubscriber(Subscriber subscriber) {
        this.subscribers.add(subscriber);
    }

    public void removeSubscriber(Subscriber subscriber) {
        this.subscribers.remove(subscriber);
    }

    public void notifier() {
        for (Subscriber sub:subscribers) {
            sub.update();
        }
    }

}
