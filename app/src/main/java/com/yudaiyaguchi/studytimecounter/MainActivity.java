package com.yudaiyaguchi.studytimecounter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DatabaseAccess databaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // pop up another activity
                Intent timerIntent = new Intent(MainActivity.this, TimerActivity.class);
                startActivity(timerIntent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


//        GraphView graph = (GraphView) findViewById(R.id.graph);
//        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
//                new DataPoint(0, 1),
//                new DataPoint(1, 5),
//                new DataPoint(2, 3)
//        });
//        graph.addSeries(series);

        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();

        ActivityTypeUtil.clearActivityTypes();
        for(ActivityType activityType : databaseAccess.getActivityTypes()) {
            ActivityTypeUtil.addActivityByType(activityType);
        }


        long aWeekAgo = System.currentTimeMillis()- TimeUnit.DAYS.toMillis(7);

        ActivityReport activityReport = databaseAccess.getGraphData();
       // Map<Long, List<ActivityTime>> activities = ActivityReport.getFilteredActivities(activityReport.getActivities(), aWeekAgo);

        Map<Long, List<ActivityTime>> activities = activityReport.getActivities();
//        Map<String,Double> data = new Hashtable<String, Double>();

        // assume that there are bunch of date map(date, money)

        double y,x;
        x = -5.0;

//        ArrayList<String> cars = new ArrayList<String>();

        // I don't even need to query all the dates
        // dates should start from when the user did the activity.
        // if there is no activity at that day, the score should be 0 and score should be negative too.

        GraphView graph = (GraphView) findViewById(R.id.graph);
//        graph.getViewport().setScalableY(true);

       // graph.getViewport().setScalable(true); // enables horizontal scrolling

        DataPoint[] points = new DataPoint[activities.size()];
        int i = 0;
        for(long key : activities.keySet()) {
            List<ActivityTime> values = activities.get(key);
            double sum = 0;
            for (ActivityTime activityTime : values) {
                sum += (double)(activityTime.getPeriod() * activityTime.getActivityType().getPrice())/60.0/60.0;
            }
            Log.i("test", "key = "+new Date(key)+", value = "+sum);
            points[i] = new DataPoint(new Date(key), sum);
            i++;
        }


        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(points);
        graph.addSeries(series);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

        series.setDrawDataPoints(true);
        series.setDataPointsRadius(15);
        series.setThickness(8);


        Spinner spinner = findViewById(R.id.spinner);
        SpinnerAdapter adapter = new SpinnerAdapter(activities.keySet());
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(adapter);

        //  graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));

    }

    class SpinnerAdapter extends BaseAdapter implements AdapterView.OnItemSelectedListener {

        ArrayList<Long> items;

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        public SpinnerAdapter(Set<Long> items) {
            this.items = new ArrayList<>();
            this.items.addAll(items);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return items.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
            }
            TextView textView = convertView.findViewById(android.R.id.text1);
            Date date = new Date(items.get(position));
            textView.setText(df.format(date));
              return convertView;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetDialog();
            Bundle bundle = new Bundle();
            bundle.putLong("time", items.get(position));
            bottomSheetDialogFragment.setArguments(bundle);
            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
