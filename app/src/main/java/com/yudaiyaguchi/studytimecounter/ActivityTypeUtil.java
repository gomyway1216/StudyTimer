package com.yudaiyaguchi.studytimecounter;

import java.util.ArrayList;

public class ActivityTypeUtil {

    private static ArrayList<ActivityType> activityTypes = new ArrayList<>();

    public static void clearActivityTypes() {
        activityTypes.clear();
    }

    public static void addActivityByType(ActivityType activityType) {
        activityTypes.add(activityType);
    }

    public static ActivityType getActivityByType(int activityId) {
        for(ActivityType activityType : activityTypes ) {
            if(activityType.activityId == activityId) {
                return activityType;
            }
        }

        return null;
    }

}
