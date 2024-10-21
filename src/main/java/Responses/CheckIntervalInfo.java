package Responses;

import java.util.concurrent.TimeUnit;

public class CheckIntervalInfo {
    private long interval;
    private TimeUnit timeUnit;
    private String lastCheck;
    private long timeUntilNextCheck;

    public CheckIntervalInfo(long interval, TimeUnit timeUnit, String lastCheck, long timeUntilNextCheck) {
        this.interval = interval;
        this.timeUnit = timeUnit;
        this.lastCheck = lastCheck;
        this.timeUntilNextCheck = timeUntilNextCheck;
    }

    public CheckIntervalInfo(long interval2, TimeUnit timeUnit2, String string) {
        // TODO Auto-generated constructor stub
    }

    public long getInterval() {
        return interval;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public String getLastCheck() {
        return lastCheck;
    }

    public long getTimeUntilNextCheck() {
        return timeUntilNextCheck;
    }
}
