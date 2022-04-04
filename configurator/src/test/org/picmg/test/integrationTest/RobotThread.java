package org.picmg.test.integrationTest;

import javafx.application.Platform;

public class RobotThread {
    private Runnable runnable;
    private int delay;
    private RobotThread next = null;
    private RobotThread prev = null;
    private volatile boolean wasStarted = false;
    private volatile boolean isFinished = false;

    private RobotThread(int delay, Runnable runnable) {
        this.runnable = runnable;
        this.delay = delay;
    }

    private RobotThread(int delay, Runnable runnable, RobotThread prev) {
        this(delay, runnable);
        this.prev = prev;
    }


    public static RobotThread build(int delay, Runnable runnable) {
        return new RobotThread(delay, runnable);
    }

    /**
     * Run one or many runnables with equivalent, non-FX delay time between sequential deployments to FX thread queue.
     * @param delay The wait time between each thread in milliseconds (e.g. delay of 1000 is 1 second)
     * @param runnables A list of runnable objects to execute on the FX thread after some delay
     */
    public static RobotThread build(int delay, Runnable... runnables) {
        if (runnables.length < 1) return null;
        if (runnables.length == 1) return build(delay, runnables[0]);
        RobotThread head = new RobotThread(0, runnables[0]);
        RobotThread cursor = head;
        // create chain of cursors
        for (int i = 1; i < runnables.length; i++) {
            cursor = new RobotThread(delay, runnables[i], cursor);
        }
        return head;
    }

    /**
     * Run the given function with FX threading after delaying on a generic Java thread. This threading hot potato
     * gives the FX threads processing time so UI components can catch up before the function is added to the FX thread
     * queue.
     * Calling this on any instance in a chain will only execute the first of the chain.
     */
    public void run() {
        if (prev != null && !prev.wasStarted) {
            // if this is not first in chain and previous has not run, start it
            prev.run();
            return;
        }
        if (prev != null && !prev.isFinished) {
            // if this is not first in chain and previous has run, wait for it to finish and call
            return;
        }
        // lock to execute new thread only once
        if (wasStarted) {
            return;
        }
        wasStarted = true;
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                Platform.runLater(() -> {
                    if (runnable != null) runnable.run();
                    isFinished = true;
                    if (next != null) {
                        next.run();
                    }
                });
            } catch (InterruptedException e) {
                System.out.println("Exception in wait queue. "); e.printStackTrace();
            }
        }).start();
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
