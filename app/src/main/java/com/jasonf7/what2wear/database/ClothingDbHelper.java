package com.jasonf7.what2wear.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jasonf7 on 26/05/15.
 */
public class ClothingDbHelper extends SQLiteOpenHelper {
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String BLOB_TYPE = " BLOB";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ClothingContract.ClothingEntry.TABLE_NAME + " (" +
                    ClothingContract.ClothingEntry._ID + " INTEGER PRIMARY KEY," +
                    ClothingContract.ClothingEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    ClothingContract.ClothingEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    ClothingContract.ClothingEntry.COLUMN_NAME_PREFERENCE + INTEGER_TYPE + COMMA_SEP +
                    ClothingContract.ClothingEntry.COLUMN_NAME_TYPE + TEXT_TYPE + COMMA_SEP +
                    ClothingContract.ClothingEntry.COLUMN_NAME_IMAGE + BLOB_TYPE +
            " )";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ClothingContract.ClothingEntry.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Clothing.db";

    public ClothingDbHelper(Context context){
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

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


}
