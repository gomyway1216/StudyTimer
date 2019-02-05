package com.yudaiyaguchi.studytimecounter;

public class ActivityTime {
    ActivityType activityType;
    long time;
    int period;

    public ActivityTime(ActivityType activityType, long time, int period) {
        this.activityType = activityType;
        this.time = time;
        this.period = period;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public long getTime() {
        return time;
    }

    public int getPeriod() {
        return period;
    }



}
