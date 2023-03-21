package com.company.checkers;

import java.util.Timer;
import java.util.TimerTask;

public class MoveTimer {
    private final Timer timer;
    private int seconds;

    MoveTimer() {
        timer = new Timer();
        seconds = 0;
    }

    public void startTimer() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                seconds++;
            }
        }, 0, 1000);
    }

    public int getSeconds() {
        return seconds;
    }

    public Timer getTimer() {
        return timer;
    }
}
