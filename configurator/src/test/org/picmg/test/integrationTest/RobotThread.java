package org.picmg.test.integrationTest;

import javafx.application.Platform;

public class RobotThread {
    private final Runnable runnable;
    private int delay;
    private RobotThread next = null;
    private RobotThread prev = null;
    private volatile boolean wasStarted = false;

    /**
     * Starts a thread chain with no runner. This is intended as the optional head of a new chain.
     */
    public RobotThread() {
        this(0, null);
    }

    /**
     * Starts a thread chain with an executable.
     * @param delay The time in milliseconds to run a generic thread before queueing the runnable as an FX thread.
     * @param runnable Function to execute.
     */
    public RobotThread(int delay, Runnable runnable) {
        this.runnable = runnable;
        this.delay = delay;
    }

    /**
     * Appends a runnable to the previous RobotThread. Called from then().
     * @param delay The time in milliseconds to run a generic thread before queueing the runnable as an FX thread.
     * @param runnable Function to execute.
     * @param prev The previous threaded item that will execute this item after completing.
     */
    private RobotThread(int delay, Runnable runnable, RobotThread prev) {
        this.runnable = runnable;
        this.delay = delay;
        this.prev = prev;
    }

    /**
     * Run the given function with FX threading after delaying on a generic Java thread. This threading hot potato
     * gives the FX threads processing time so UI components can catch up before the function is added to the FX thread
     * queue.
     * Calling this on any instance in a chain will only execute the first of the chain.
     */
    public void run() {
        // if this is not first in chain and previous has not run, start it
        if (prev != null && !prev.wasStarted) {
            prev.run();
            return;
        }

        // lock to execute new thread only once
        if (wasStarted) {
            return;
        }
        wasStarted = true;

        // skip if head empty
        if (runnable == null && delay == 0) {
            if (next != null) {
                next.run();
            }
            return;
        } else {
            new Thread(() -> {
                try {
                    Thread.sleep(delay);
                    Platform.runLater(() -> {
                        if (runnable != null) runnable.run();
                        if (next != null) {
                            next.run();
                        }
                    });
                } catch (InterruptedException e) {
                    System.out.println("Exception in wait queue. "); e.printStackTrace();
                }
            }).start();
        }
    }

    public RobotThread then(Runnable runnable) {
        return then(0, runnable);
    }

    public RobotThread then(int delay, Runnable runnable) {
        // pass down chain until end
        if (next != null) {
            return next.then(delay, runnable);
        }
        // append to end of chain
        next = new RobotThread(delay, runnable, this);
        return next;
    }

    public RobotThread wait(int delay) {
        next = new RobotThread(delay, null, this);
        return next;
    }

}
