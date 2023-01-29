package sbc.gui;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class DrawLoop {
    // status of the loop
    private final AtomicBoolean running;

    // variable to allow pausing of the loop
    private final AtomicBoolean paused;

    // thread on which the loop runs
    private Thread loopThread;

    public DrawLoop() {
        loopThread = null;
        running = new AtomicBoolean(false);
        paused = new AtomicBoolean(false);
    }

    public synchronized void start() {
        // do not allow starting of more than 1 thread at a time
        if (loopThread != null && loopThread.isAlive())
            return;

        loopThread = new Thread(this::loop);

        loopThread.start();
        running.set(true);
    }

    public synchronized void pause() {
        paused.set(true);
    }

    public synchronized void resume() {
        paused.set(false);
    }

    private void loop() {
        while (running.get()) {
            if (!paused.get()) {
                update();
                render();
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    // do nothing.
                }
            } else {
                // if paused go to sleep instead of busy waiting
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // do nothing
                }
            }
        }
    }

    protected abstract void update();
    protected abstract void render();
}
