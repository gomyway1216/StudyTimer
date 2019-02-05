package com.yudaiyaguchi.studytimecounter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;

    /**
     * Private constructor to aboid object creation from outside classes.
     *
     * @param context
     */
    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseAssetOpenHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.database = openHelper.getWritableDatabase();

        Cursor cursor = null;
        try {
            cursor = database.rawQuery("SELECT * FROM Record", null);
            if(cursor.getCount() == 0) {
                Date date = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDate = df.format(date);
                database.execSQL("INSERT INTO Record(ActionTime, TotalTime, CurrentMoney, Note) VALUES(?,?,?,?)",
                        new String[]{formattedDate, String.valueOf(0), String.valueOf(0), "start"});
            }
        } catch(Exception e) {
            Log.d("Error!!!", e.getMessage());
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    /**
     * Read all quotes from the database.
     *
     * @return a List of quotes
     */
//    public List<String> getQuotes() {
//        List<String> list = new ArrayList<>();
//        Cursor cursor = database.rawQuery("SELECT * FROM quotes", null);
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//            list.add(cursor.getString(0));
//            cursor.moveToNext();
//        }
//        cursor.close();
//        return list;
//    }

    public int getPrice(int actionID) {
        Cursor cursor = null;
        try {
            cursor = database.rawQuery("SELECT * FROM ActivityType WHERE ActivityID=?",
                    new String[] {String.valueOf(actionID)});
            int price = 0;
            cursor.moveToFirst();
            price = cursor.getInt(2);
//            cursor.close();

            Log.i("test", "price from database "+price);
            return price;
        } catch (Exception e) {
            Log.d("Error!!!", e.getMessage());
            return 0;
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }
    }

    public String getActivityDescription(int actionID) {
        Cursor cursor = null;
        try {
            cursor = database.rawQuery("SELECT ActivityName FROM ActivityType WHERE ActivityID=?",
                    new String[] {String.valueOf(actionID)});
            cursor.moveToFirst();
            String activityName = cursor.getString(0);
//            cursor.close();
            return activityName;
        } catch (Exception e) {
            Log.d("Error!!!", e.getMessage());
            return "";
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }
    }

    public Map<Integer,Integer> getDailyActivity(String searchTime) {
        Cursor cursor = null;
        try {
            // this could be empty
            cursor = database.rawQuery("SELECT ActivityID, SUM(Period) FROM ActivityTime " +
                            "WHERE StartTime=? GROUP BY ActivityID ORDER BY ActivityID",
                    new String[]{searchTime + "%"});
            Map<Integer,Integer> results = new Hashtable<Integer, Integer>();
            if(cursor.moveToFirst()) {
                while(cursor.isAfterLast()) {
                    results.put(cursor.getInt(0), cursor.getInt(1));
                    cursor.moveToNext();
                }
            }
            return results;
        } catch(Exception e) {
            Log.d("Error!!!", e.getMessage());
            return null;
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }
    }

//    public Map<Integer,Integer> getDailyActivity(ArrayList<String> searchTimes) {
//        Cursor cursor = null;
//        try {
//            // this could be empty
//            cursor = database.rawQuery("SELECT ActivityID, SUM(Period) FROM ActivityTime " +
//                            "WHERE StartTime=? GROUP BY ActivityID ORDER BY ActivityID",
//                    new String[]{searchTime + "%"});
//            Map<Integer,Integer> results = new Hashtable<Integer, Integer>();
//            if(cursor.moveToFirst()) {
//                while(cursor.isAfterLast()) {
//                    results.put(cursor.getInt(0), cursor.getInt(1));
//                    cursor.moveToNext();
//                }
//            }
//            return results;
//        } catch(Exception e) {
//            Log.d("Error!!!", e.getMessage());
//            return null;
//        } finally {
//            if(cursor != null) {
//                cursor.close();
//            }
//        }
//    }

//    public void subtractMoney(Double money) {
//        Cursor cursor = null;
//        try {
//            cursor = database.rawQuery("SELECT CurrentMoney FROM Record LIMIT 1",null);
//            cursor.moveToFirst();
//            double currentMoney = cursor.getFloat(0);
//            currentMoney -= money;
//            database.execSQL("INSERT INTO ActivityTime(ActivityID, StartTime, EndTime, Period) VALUES(?,?,?,?)",
//                    new String[]{String.valueOf(activityID), startTime, endTime, String.valueOf(period)});
//
//        } catch(Exception e) {
//            Log.d("Error!!!", e.getMessage());
//        } finally {
//            if(cursor != null) {
//                cursor.close();
//            }
//        }
//    }

    public Map<Integer,Integer> getAllDate(String searchTime) {
        Cursor cursor = null;
        try {
            // this could be empty
            cursor = database.rawQuery("SELECT ActionTime FROM Record",null);
            Map<Integer,Integer> results = new Hashtable<Integer, Integer>();
            ArrayList<String> cars = new ArrayList<String>();

            if(cursor.moveToFirst()) {
                while(cursor.isAfterLast()) {
                    results.put(cursor.getInt(0), cursor.getInt(1));
                    cursor.moveToNext();
                }
            }
            return results;
        } catch(Exception e) {
            Log.d("Error!!!", e.getMessage());
            return null;
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }
    }



    public Map<Integer,Integer> getAllDailyData(String searchTime) {
        Cursor cursor = null;
        try {
            // this could be empty
            cursor = database.rawQuery("SELECT ActivityID, SUM(Period) FROM ActivityTime " +
                            "WHERE StartTime=? GROUP BY ActivityID ORDER BY ActivityID",
                    new String[]{searchTime + "%"});
            Map<Integer,Integer> results = new Hashtable<Integer, Integer>();
            if(cursor.moveToFirst()) {
                while(cursor.isAfterLast()) {
                    results.put(cursor.getInt(0), cursor.getInt(1));
                    cursor.moveToNext();
                }
            }
            return results;
        } catch(Exception e) {
            Log.d("Error!!!", e.getMessage());
            return null;
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }
    }

    public void modifyCurrentMoney(double price, String description) {
        Cursor cursor = null;
        try {
//            Log.d("test", "recordTotalActivity() called with: activityID = [" + activityID + "], endTime = [" + endTime + "], period = [" + period + "]");
            cursor = database.rawQuery("SELECT TotalTime, CurrentMoney FROM Record" +
                    " ORDER BY ActionTime DESC LIMIT 1", null);
            cursor.moveToFirst();
            int time = cursor.getInt(0);
            int money = cursor.getInt(1);
//            cursor.close();

//            time += period;
            money -= price;

            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = df.format(date);

            database.execSQL("INSERT INTO Record(ActionTime, TotalTime, CurrentMoney, Note) VALUES(?,?,?,?)",
                    new String[]{formattedDate, String.valueOf(time), String.valueOf(money), description});
        } catch(Exception e) {
            Log.d("Error!!!", e.getMessage());
        }  finally {
            if(cursor != null) {
                cursor.close();
            }
        }
    }

//    public boolean checkDuplicate(String startTime) {
//        try {
//            // this could be empty
//            Cursor cursor = database.rawQuery("SELECT ActivityID, SUM(Period) FROM ActivityTime " +
//                            "WHERE StartTime=?", new String[]{startTime});
//            if(cursor.moveToFirst()) {
//                return true;
//            }
//            return false;
//        } catch(Exception e) {
//            Log.d("Error!!!", e.getMessage());
//            return false;
//        }
//    }

    public void recordTotalActivity(String endTime, int activityID, int period) {
        try {
            Log.d("test", "recordTotalActivity() called with: activityID = [" + activityID + "], endTime = [" + endTime + "], period = [" + period + "]");
            Cursor cursor = database.rawQuery("SELECT TotalTime, CurrentMoney FROM Record", null);
            cursor.moveToLast();
            int time = cursor.getInt(0);
            float money = (float) cursor.getFloat(1);
//            cursor.close();

            time += period;
            money +=
                    (float)getPrice(activityID) * (float)period / 60.0f / 60.0f;
            String activityName = getActivityDescription(activityID);

            Log.i("test",money+"" );


            database.execSQL("INSERT INTO Record(ActionTime, TotalTime, CurrentMoney, Note) VALUES(?,?,?,?)",
                    new String[]{endTime, String.valueOf(time), String.valueOf(money), activityName});
        } catch(Exception e) {
            Log.d("Error!!!", e.getMessage());
        }
    }


    public void recordActivity(int activityID, String startTime, String endTime, int period) {
        Log.d("test", "recordActivity() called with: activityID = [" + activityID + "], startTime = [" + startTime + "], endTime = [" + endTime + "], period = [" + period + "]");
        //Log.d("inside of recordAnswer", "inside of recordAnswer");

//        boolean dup = checkDuplicate(startTime);
//        if (dup) {
//            Log.d("reocrd already", "reocrd already");
//        } else {

            database.execSQL("INSERT INTO ActivityTime(ActivityID, StartTime, EndTime, Period) VALUES(?,?,?,?)",
                    new String[]{String.valueOf(activityID), startTime, endTime, String.valueOf(period)});
//        }
        recordTotalActivity(endTime, activityID, period);

    }

    public ArrayList<ActivityType> getActivityTypes() {
        ArrayList<ActivityType> activityTypes = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery("SELECT * FROM ActivityType", null);
            int price = 0;
            while(cursor.moveToNext()) {
                activityTypes.add(new ActivityType(cursor.getInt(0), cursor.getString(1),cursor.getInt(2)));
            }
//            cursor.close();

            Log.i("test", "price from database "+price);
            return activityTypes;
        } catch (Exception e) {
            Log.d("Error!!!", e.getMessage());
           return null;
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }

    }

    public ActivityReport getGraphData(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Cursor cursor = null;
        ActivityReport activityReport = new ActivityReport();
        try {
            cursor = database.rawQuery("SELECT * FROM ACTIVITYTime", null);
            while(cursor.moveToNext()) {
                int activityType = cursor.getInt(0);
                String time = cursor.getString(1);
                int period = cursor.getInt(3);

                try {
                    ActivityTime activityTime = new ActivityTime(ActivityTypeUtil.getActivityByType(activityType),
                            df.parse(time).getTime(), period);

                    activityReport.addActivityTime(activityTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }
        catch (Exception e) {
            Log.d("Error!!!", e.getMessage());
            return null;
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }
        return activityReport;
    }

}