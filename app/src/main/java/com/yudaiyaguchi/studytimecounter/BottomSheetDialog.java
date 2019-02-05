package com.yudaiyaguchi.studytimecounter;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };

    @Override
    public void setupDialog(Dialog dialog, int style) {
        Bundle bundle = getArguments();
        if (bundle == null) {
            dismiss();
            return;
        }
        
        long time = bundle.getLong("time");

        final View contentView = View.inflate(getContext(), R.layout.bottom_sheet, null);
        dialog.setContentView(contentView);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getContext());
        databaseAccess.open();

        ActivityTypeUtil.clearActivityTypes();
        for(ActivityType activityType : databaseAccess.getActivityTypes()) {
            ActivityTypeUtil.addActivityByType(activityType);
        }

        ActivityReport activityReport = databaseAccess.getGraphData();
        List<ActivityTime> activityTimes = activityReport.getActivities().get(time);

        HashMap<Integer, Integer> activityIdToDurationMap = new HashMap<>();
        for(int i = 1; i< 10; i++) {
            activityIdToDurationMap.put(i, 0);
        }

        double totalPrice = 0;
        int totalDuration = 0;
        
        for(ActivityTime activityTime : activityTimes) {
            int duration = activityIdToDurationMap.get(activityTime.getActivityType().getActivityId());
            duration+= activityTime.getPeriod();
            totalPrice += (double) (activityTime.getPeriod() * activityTime.getActivityType().getPrice() )/ 60.0 /60.0;
            totalDuration+=activityTime.getPeriod();
            activityIdToDurationMap.put(activityTime.getActivityType().getActivityId(), duration);
        }

        TextView totalPriceTV = contentView.findViewById(R.id.total_money);
        totalPriceTV.setText(String.format("$%.2f", totalPrice));

        TextView totalDurationTV = contentView.findViewById(R.id.total_time);
        int hours = totalDuration / 3600;
        int minutes = (totalDuration % 3600) / 60;
        int seconds = totalDuration % 60;

        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        
        totalDurationTV.setText(timeString);
        
        setTextToRow(R.id.tv_activity_1, R.id.tv_time_1, contentView, ActivityTypeUtil.getActivityByType(1), activityIdToDurationMap.get(1));
        setTextToRow(R.id.tv_activity_2, R.id.tv_time_2, contentView, ActivityTypeUtil.getActivityByType(2), activityIdToDurationMap.get(2));
        setTextToRow(R.id.tv_activity_3, R.id.tv_time_3, contentView, ActivityTypeUtil.getActivityByType(3), activityIdToDurationMap.get(3));
        setTextToRow(R.id.tv_activity_4, R.id.tv_time_4, contentView, ActivityTypeUtil.getActivityByType(4), activityIdToDurationMap.get(4));
        setTextToRow(R.id.tv_activity_5, R.id.tv_time_5, contentView, ActivityTypeUtil.getActivityByType(5), activityIdToDurationMap.get(5));
        setTextToRow(R.id.tv_activity_6, R.id.tv_time_6, contentView, ActivityTypeUtil.getActivityByType(6), activityIdToDurationMap.get(6));
        setTextToRow(R.id.tv_activity_7, R.id.tv_time_7, contentView, ActivityTypeUtil.getActivityByType(7), activityIdToDurationMap.get(7));
        setTextToRow(R.id.tv_activity_8, R.id.tv_time_8, contentView, ActivityTypeUtil.getActivityByType(8), activityIdToDurationMap.get(8));
        setTextToRow(R.id.tv_activity_9, R.id.tv_time_9, contentView, ActivityTypeUtil.getActivityByType(9), activityIdToDurationMap.get(9));

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if( behavior != null && behavior instanceof BottomSheetBehavior ) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        TextView title = contentView.findViewById(R.id.title);
        title.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(time)));
        
    }
    
    private void setTextToRow(int activityIdTextViewId, int durationTextViewId, View view, ActivityType activityType, int duration) {
        TextView activityIdTextView = view.findViewById(activityIdTextViewId);
        TextView durationTextView = view.findViewById(durationTextViewId);
        
        activityIdTextView.setText(activityType.activityName);
        int hours = duration / 3600;
        int minutes = (duration % 3600) / 60;
        int seconds = duration % 60;

        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        durationTextView.setText(timeString);

    }
}