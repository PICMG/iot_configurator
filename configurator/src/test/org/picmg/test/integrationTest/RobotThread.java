package org.picmg.test.integrationTest;

import javafx.application.Platform;

import java.awt.*;

public class RobotThread {
    private static int I = 0;
    private static boolean isDevelop = false;
    private final Runnable runnable;
    private int delay;
    private RobotThread next = null;
    private RobotThread prev = null;
    private volatile boolean wasStarted = false;
    private int index = I++;

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

    private void devlog(String message) {
        if (isDevelop) System.out.println(message);
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
        devlog("FOUND HEAD TO RUN");
        // lock to execute new thread only once
        if (wasStarted) {
            return;
        }
        wasStarted = true;
        devlog("HEAD WASNT STARTED");

        // skip threading if empty and no delay
        if (runnable == null && delay == 0) {
            devlog("EMPTY");
            if (next != null) {
                devlog("RUNNING NEXXT");
                next.run();
            }
            return;
        } else {
            new Thread(() -> {
                try {
                    Thread.sleep(delay);
                    Platform.runLater(() -> {
                        if (runnable != null) {
                            devlog("RUNNING RUNNABLE");
                            runnable.run();
                        }
                        if (next != null) {
                            devlog("running next");
                            next.run();
                        } else System.out.println("NOT RUNNING NEXT");
                    });
                } catch (InterruptedException e) {
                    System.out.println("Exception in wait queue. "); e.printStackTrace();
                }
            }).start();
        }
    }

    private RobotThread getHead() {
        if (prev != null) {
//            System.out.println(this.index + " is not head");
            return prev.getHead();
        }
//        System.out.println(this.index + " is head");
        return this;

    }

    private RobotThread getTail() {
        if (next != null) {
            return next.getTail();
        }
        return this;
    }

    public RobotThread then(RobotThread robotThread) {
        RobotThread thisTail = getTail();
        RobotThread thatHead = robotThread.getHead();
        thisTail.next = thatHead;
        thatHead.prev = thisTail;
        return getHead();
    }

    public RobotThread then(int delay, Runnable runnable) {
        return then(new RobotThread(delay, runnable));
    }

    public RobotThread wait(int delay) {
        return then(new RobotThread(delay, null));
    }

    public void printAll() {
        RobotThread head = getHead();
        System.out.println(head.toString());
        while (head != null) {
            System.out.println(head.toString());
            head = head.next;
        }
    }

    public String toString() {
        String out = this.index + "\t" + "delay= " + delay + "\trunnable= " + (runnable == null ? "???" : runnable.toString());
        out += "\tNEXT = " + (next == null ? "???" : next.index) + "\tPREVIOUS = " + (prev == null ? "???" : prev.index);
        return out;
    }

}
