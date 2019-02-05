package com.yudaiyaguchi.studytimecounter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class ActivityReport {
    private TreeMap<Long, List<ActivityTime>> activities;
    private long startTime;
    public ActivityReport() {
        activities = new TreeMap<>();
        startTime = -1;
    }

    public ActivityReport( long startTime) {
        this();
        this.startTime = startTime;
    }

    public void addActivityTime(ActivityTime activityTime) {
        long dateMidnight = activityTime.getTime()- (activityTime.getTime() % TimeUnit.DAYS.toMillis(1));
        if(startTime != -1 && dateMidnight < startTime) {
            return;
        }
        if(activities.containsKey(dateMidnight)) {
            activities.get(dateMidnight).add(activityTime);
        }
        else {
            ArrayList<ActivityTime> activityList = new ArrayList<>();
            activityList.add(activityTime);

            activities.put(dateMidnight, activityList);
        }
    }

    public TreeMap<Long, List<ActivityTime>> getActivities() {
        return activities;
    }

    public static Map<Long, List<ActivityTime>> getFilteredActivities(Map<Long, List<ActivityTime>> map, long startTime) {
        TreeMap<Long, List<ActivityTime>> filterMap = new TreeMap<>();
        for(Long key : map.keySet()) {
            if(key >= startTime) {
                filterMap.put(key, map.get(key));
            }
        }
        return filterMap;
    }
}
