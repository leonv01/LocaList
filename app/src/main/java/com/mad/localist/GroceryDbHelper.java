package com.mad.localist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

public class GroceryDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "GroceryList.db";


    public static final String GROCERY_TABLE = "entry";
    public static final String GROCERY_COL_ARTICLE = "article";
    public static final String GROCERY_COL_DESCRIPTION = "description";
    public static final String GROCERY_COL_QUANTITY = "quantity";
    public static final String GROCERY_COL_LOCATION = "location";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + GROCERY_TABLE + " ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY,"
            + GROCERY_COL_ARTICLE + " TEXT,"
            + GROCERY_COL_DESCRIPTION + " TEXT,"
            + GROCERY_COL_QUANTITY + " TEXT)";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + GROCERY_TABLE;

    public GroceryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
