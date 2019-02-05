package com.yudaiyaguchi.studytimecounter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class TimerActivity extends AppCompatActivity {

    TextView timeView;
    TextView moneyView;
    TextView totalStudyTimeView;
    TextView totalWorkTimeView;
    TextView totalGroupTimeView;
    TextView totalLectureTimeView;
    TextView totalSportsTimeView;
    TextView totalTalkingTimeView;
    TextView totalRestingTimeView;
    TextView totalOtherTimeView;
    TextView totalCustomTimeView;


    AtomicBoolean isInEditMode = new AtomicBoolean(false);


    private RadioGroup radioActionTypeGroup;

    private RadioGroup actionType1Group;
    private RadioGroup actionType2Group;
    private RadioGroup actionType3Group;


    Button startButton;
    Button pauseButton;
    Button resetButton;
    Button saveLapButton;
    Button endButton;

    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;

    double sumMoney = 0.0;
    double tempMoney = 0.0;

    Handler handler;

    int Seconds, Minutes, Hours, MilliSeconds;

    ListView listView;

    String[] ListElements = new String[] {  };

    List<String> ListElementsArrayList;

    ArrayAdapter<String> adapter;

    int moneyPerHour;

    private boolean isChecking = true;
    private int mCheckedId = R.id.study;


    int totalStudySeconds = 0;

    int totalWorkSeconds = 0;

    int totalGroupSeconds = 0;

    int totalLectureSeconds = 0;

    int totalSportsSeconds = 0;

    int totalTalkingSeconds = 0;

    int totalRestingSeconds = 0;

    int totalOtherSeconds = 0;

    int totalCustomSeconds = 0;

    boolean beingPaused = true;

    DatabaseAccess databaseAccess;

//    Date previousStartTime;
//    Date previousEndTime;
//    Date nextStartTime;
//    Date nextEndTime;

    Date startDate;
    Date endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);



//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        Log.d("Hey, line 116", "Hey, line 116");
        // get the access to database access class
        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();

        ActivityTypeUtil.clearActivityTypes();
        for(ActivityType activityType : databaseAccess.getActivityTypes()) {
            ActivityTypeUtil.addActivityByType(activityType);
        }



        Log.d("Hey, line 121", "Hey, line 121");

        timeView = findViewById(R.id.timeView);
        moneyView = findViewById(R.id.moneyView);

        totalStudyTimeView = findViewById(R.id.totalStudyTimeView);
        totalWorkTimeView = findViewById(R.id.totalWorkTimeView);
        totalGroupTimeView = findViewById(R.id.totalGroupTimeView);
        totalLectureTimeView = findViewById(R.id.totalLectureTimeView);
        totalSportsTimeView = findViewById(R.id.totalSportsTimeView);
        totalTalkingTimeView = findViewById(R.id.totalTalkingTimeView);
        totalRestingTimeView = findViewById(R.id.totalRestingTimeView);
        totalOtherTimeView = findViewById(R.id.totalOtherTimeView);
        totalCustomTimeView = findViewById(R.id.totalCustomTimeView);

        handler = new Handler();

        ListElementsArrayList = new ArrayList<String>(Arrays.asList(ListElements));

        adapter = new ArrayAdapter<String>(TimerActivity.this,
                android.R.layout.simple_list_item_1,
                ListElementsArrayList
        );

//        radioActionTypeGroup = (RadioGroup) findViewById(R.id.actionType);


        actionType1Group = (RadioGroup) findViewById(R.id.actionType1);
        actionType2Group = (RadioGroup) findViewById(R.id.actionType2);
        actionType3Group = (RadioGroup) findViewById(R.id.actionType3);

        RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final RadioGroup group, int checkedId) {
                if(isInEditMode.get()) {
                    return;
                }

                isInEditMode.set(true);

                // saving data ///////////////////////////////////////////////////////////////////
                int acType = 0;
                if(mCheckedId == R.id.study)    acType  = 1;
                else if(mCheckedId == R.id.work)    acType  = 2;
                else if(mCheckedId == R.id.group)    acType  = 3;
                else if(mCheckedId == R.id.lecture)    acType  = 4;
                else if(mCheckedId == R.id.sports)    acType  = 5;
                else if(mCheckedId == R.id.talking)    acType  = 6;
                else if(mCheckedId == R.id.resting)    acType  = 7;
                else if(mCheckedId == R.id.other)    acType  = 8;
                else if(mCheckedId == R.id.custom)    acType  = 9;

                if(!beingPaused)
                    endDate = Calendar.getInstance().getTime();

                if(endDate != null && startDate != null) {
                    long diffInMs = endDate.getTime() - startDate.getTime();

                    int diffInSec = (int) TimeUnit.MILLISECONDS.toSeconds(diffInMs);
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedStart = df.format(startDate);
                    String formattedEnd = df.format(endDate);

//                if(TimeBuff != 0L)
                    databaseAccess.recordActivity(acType, formattedStart, formattedEnd, diffInSec);
                }
                ///////////////////////////////////////////////////////////////////////////////////

                startDate = Calendar.getInstance().getTime();

                List<RadioGroup> radioGroups = Arrays.asList(actionType1Group, actionType2Group, actionType3Group);
                for(RadioGroup rg : radioGroups) {
                    if(rg.getId() != group.getId()) {
                        rg.clearCheck();
                    }
                }

                //isChecking = true;

                checkActivityType();
                resetTimer();

                mCheckedId = checkedId;

                isInEditMode.set(false);
            }

        };

        actionType1Group.setOnCheckedChangeListener(onCheckedChangeListener);
        actionType2Group.setOnCheckedChangeListener(onCheckedChangeListener);
        actionType3Group.setOnCheckedChangeListener(onCheckedChangeListener);

        startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startDate = Calendar.getInstance().getTime();

                StartTime = SystemClock.uptimeMillis();
                handler.postDelayed(runnable, 0);

                resetButton.setEnabled(false);
                startButton.setEnabled(false);

                beingPaused = false;
            }
        });

        pauseButton = findViewById(R.id.pauseButton);

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // set end timer
                endDate = Calendar.getInstance().getTime();

                TimeBuff += MillisecondTime;

                sumMoney += tempMoney;
                checkActivityType();

                handler.removeCallbacks(runnable);

                resetButton.setEnabled(true);
                startButton.setEnabled(true);

                beingPaused = true;
            }
        });

        resetButton = findViewById(R.id.resetButton);
        // needs pop up

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // I think this part also needs to store -> reset means delete the stuff

                MillisecondTime = 0L;
                StartTime = 0L;
                TimeBuff = 0L;
                UpdateTime = 0L;
                Seconds = 0;
                Minutes = 0;
                Hours = 0;
                MilliSeconds = 0;

                timeView.setText("00:00:00");
                moneyView.setText("0");
                sumMoney = 0.0;

                ListElementsArrayList.clear();

                adapter.notifyDataSetChanged();
                startButton.setEnabled(true);
            }
        });

        saveLapButton = findViewById(R.id.saveLapButton);

        saveLapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ListElementsArrayList.add(timeView.getText().toString());

                adapter.notifyDataSetChanged();

            }
        });

        endButton = findViewById(R.id.endButton);

        listView = (ListView)findViewById(R.id.listview1);
        listView.setAdapter(adapter);
    }

    public void resetTimer() {
        sumMoney += tempMoney;

        tempMoney = 0.0;

        handler.removeCallbacks(runnable);

        MillisecondTime = 0L;
        StartTime = 0L;
        TimeBuff = 0L;
        UpdateTime = 0L;
        Seconds = 0;
        Minutes = 0;
        Hours = 0;
        MilliSeconds = 0;

        timeView.setText("00:00:00");

        if(!beingPaused) {
            StartTime = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, 0);
        }
    }


    public Runnable runnable = new Runnable() {

        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;
            UpdateTime = TimeBuff + MillisecondTime;
            Seconds = (int) (UpdateTime / 1000);
            Minutes = Seconds / 60;
            Hours = Minutes / 60;
            Minutes = Minutes % 60;
            Seconds = Seconds % 60;
            MilliSeconds = (int) (UpdateTime % 1000);
            timeView.setText("" + Hours + ":"
                    + String.format("%02d", Minutes) + ":"
                    + String.format("%02d", Seconds));

            if (mCheckedId == R.id.study) {
                changeUiForActivityType(totalStudyTimeView, totalStudySeconds, 1);
            } else if (mCheckedId == R.id.work) {
                changeUiForActivityType(totalWorkTimeView, totalWorkSeconds, 2);
            } else if (mCheckedId == R.id.group) {
                changeUiForActivityType(totalGroupTimeView, totalGroupSeconds, 3);
            } else if (mCheckedId == R.id.lecture) {
                changeUiForActivityType(totalLectureTimeView, totalLectureSeconds, 4);
            } else if (mCheckedId == R.id.sports) {
                changeUiForActivityType(totalSportsTimeView, totalSportsSeconds, 5);
            } else if (mCheckedId == R.id.talking) {
                changeUiForActivityType(totalTalkingTimeView, totalTalkingSeconds, 6);
            } else if (mCheckedId == R.id.resting) {
                changeUiForActivityType(totalRestingTimeView, totalRestingSeconds, 7);
            } else if (mCheckedId == R.id.other) {
                changeUiForActivityType(totalOtherTimeView, totalOtherSeconds, 8);
            } else if (mCheckedId == R.id.custom) {
                changeUiForActivityType(totalCustomTimeView, totalCustomSeconds, 9);
            }

            tempMoney = moneyPerHour * MillisecondTime / (1000 * 60 * 60 * 1.0);
            moneyView.setText(String.format("%.3f", sumMoney + tempMoney));

            handler.postDelayed(this, 0);

            //                int tempOtherTotal = totalOtherSeconds + (int)(MillisecondTime / 1000);
//                int tempMinutes = tempOtherTotal / 60;
//
//                int tempHours = tempMinutes / 60;
//
//                tempMinutes = tempMinutes % 60;
//
//                tempOtherTotal = tempOtherTotal % 60;
//                totalOtherTimeView.setText("" + tempHours + ":"
//                        + String.format("%02d", tempMinutes) + ":"
//                        + String.format("%02d", tempOtherTotal));
////                moneyPerHour = -10;
//                moneyPerHour = ActivityTypeUtil.getActivityByType(8).getPrice();
        }

    };

    private void changeUiForActivityType(TextView textView, int totalSeconds,  int activityId) {
        int tempTotal = totalSeconds + (int)(MillisecondTime / 1000);
        int tempMinutes = tempTotal / 60;
        int tempHours = tempMinutes / 60;
        tempMinutes %= 60;
        tempTotal %= 60;
        textView.setText("" + tempHours + ":"
                + String.format("%02d", tempMinutes) + ":"
                + String.format("%02d", tempTotal));
//                moneyPerHour = -10;
        moneyPerHour = ActivityTypeUtil.getActivityByType(activityId).getPrice();

    }

    public void checkActivityType() {
        if (mCheckedId == R.id.study) {
            Toast.makeText(this, "study", Toast.LENGTH_SHORT).show();
            totalStudySeconds += (int)(MillisecondTime / 1000);
            // write to the database, but we don't know the end time yet
        } else if (mCheckedId == R.id.work) {
            Toast.makeText(this, "work", Toast.LENGTH_SHORT).show();
            totalWorkSeconds += (int)(MillisecondTime / 1000);
        } else if (mCheckedId == R.id.group) {
            Toast.makeText(this, "group", Toast.LENGTH_SHORT).show();
            totalGroupSeconds += (int)(MillisecondTime / 1000);
        } else if (mCheckedId == R.id.lecture) {
            Toast.makeText(this, "lecture", Toast.LENGTH_SHORT).show();
            totalLectureSeconds += (int)(MillisecondTime / 1000);
        } else if (mCheckedId == R.id.sports) {
            Toast.makeText(this, "sports", Toast.LENGTH_SHORT).show();
            totalSportsSeconds += (int)(MillisecondTime / 1000);
        } else if (mCheckedId == R.id.talking) {
            Toast.makeText(this, "talking", Toast.LENGTH_SHORT).show();
            totalTalkingSeconds += (int)(MillisecondTime / 1000);
        } else if (mCheckedId == R.id.resting) {
            Toast.makeText(this, "resting", Toast.LENGTH_SHORT).show();
            totalRestingSeconds += (int)(MillisecondTime / 1000);
        } else if (mCheckedId == R.id.other) {
            Toast.makeText(this, "other", Toast.LENGTH_SHORT).show();
            totalOtherSeconds += (int)(MillisecondTime / 1000);
        } else if (mCheckedId == R.id.custom) {
            Toast.makeText(this, "custom", Toast.LENGTH_SHORT).show();
            totalCustomSeconds += (int)(MillisecondTime / 1000);
        }
    }

    public void showType(View view) {
        if (mCheckedId == R.id.study) {
            Toast.makeText(this, "study", Toast.LENGTH_SHORT).show();
        } else if (mCheckedId == R.id.work) {
            Toast.makeText(this, "work", Toast.LENGTH_SHORT).show();
        } else if (mCheckedId == R.id.group) {
            Toast.makeText(this, "group", Toast.LENGTH_SHORT).show();
        } else if (mCheckedId == R.id.lecture) {
            Toast.makeText(this, "lecture", Toast.LENGTH_SHORT).show();
        } else if (mCheckedId == R.id.sports) {
            Toast.makeText(this, "sports", Toast.LENGTH_SHORT).show();
        } else if (mCheckedId == R.id.talking) {
            Toast.makeText(this, "talking", Toast.LENGTH_SHORT).show();
        } else if (mCheckedId == R.id.resting) {
            Toast.makeText(this, "resting", Toast.LENGTH_SHORT).show();
        } else if (mCheckedId == R.id.other) {
            Toast.makeText(this, "other", Toast.LENGTH_SHORT).show();
        } else if (mCheckedId == R.id.custom) {
            Toast.makeText(this, "custom", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
//        Intent returnIntent = new Intent();
//        returnIntent.putExtra("hasBackPressed",true);
//        setResult(Activity.RESULT_OK,returnIntent);
//        finish();

        // Save data
            // saving data ///////////////////////////////////////////////////////////////////
            int acType = 0;
            if(mCheckedId == R.id.study)    acType  = 1;
            else if(mCheckedId == R.id.work)    acType  = 2;
            else if(mCheckedId == R.id.group)    acType  = 3;
            else if(mCheckedId == R.id.lecture)    acType  = 4;
            else if(mCheckedId == R.id.sports)    acType  = 5;
            else if(mCheckedId == R.id.talking)    acType  = 6;
            else if(mCheckedId == R.id.resting)    acType  = 7;
            else if(mCheckedId == R.id.other)    acType  = 8;
            else if(mCheckedId == R.id.custom)    acType  = 9;

            if(!beingPaused)
                endDate = Calendar.getInstance().getTime();

            long diffInMs = endDate.getTime() - startDate.getTime();

            int diffInSec = (int)TimeUnit.MILLISECONDS.toSeconds(diffInMs);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedStart = df.format(startDate);
            String formattedEnd = df.format(endDate);

//            if(TimeBuff != 0L)
            databaseAccess.recordActivity(acType, formattedStart, formattedEnd, diffInSec);
            ////////////////////////////////
            /// ///////////////////////////////////////////////////

        databaseAccess.close();

        Intent mainIntent = new Intent(TimerActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }
}