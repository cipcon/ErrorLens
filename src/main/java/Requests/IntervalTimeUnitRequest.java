package Requests;

import java.util.concurrent.TimeUnit;

public class IntervalTimeUnitRequest {
    long interval;
    TimeUnit timeUnit;

    public IntervalTimeUnitRequest(long interval, TimeUnit timeUnit) {
        this.interval = interval;
        this.timeUnit = timeUnit;
    }

    public long getInterval() {
        return interval;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }
}
