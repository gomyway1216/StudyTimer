package com.yudaiyaguchi.studytimecounter;

public class ActivityType {

    int activityId;
    String activityName;
    int price;

    public ActivityType(int activityId, String activityName, int price) {
        this.activityId = activityId;
        this.activityName = activityName;
        this.price = price;
    }

    public int getActivityId() {
        return activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public int getPrice() {
        return price;
    }
}
