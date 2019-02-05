package com.yudaiyaguchi.studytimecounter;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;


/**
 * Open helper for database.
 */
public class DatabaseAssetOpenHelper extends SQLiteAssetHelper {
    //    private static final String DATABASE_NAME = "quotes.db";
//    private static final String DATABASE_NAME = "questionSet.db";
    private static final String DATABASE_NAME = "STC.db";

    private static final int DATABASE_VERSION = 1;

    public DatabaseAssetOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }
}
