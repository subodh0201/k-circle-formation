package sbc.gui;

import java.util.LinkedList;

public class EventQueue {
    private final LinkedList<Event> queue = new LinkedList<>();

    // enqueue an event
    public synchronized void enqueue(Event e) {
        queue.addLast(e);
    }

    // dequeue an event
    public synchronized Event dequeue() {
        return queue.removeFirst();
    }

    public synchronized int size() {
        return queue.size();
    }
}
