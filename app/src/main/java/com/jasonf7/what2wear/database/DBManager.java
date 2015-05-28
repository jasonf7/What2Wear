package com.jasonf7.what2wear.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

/**
 * Created by jasonf7 on 26/05/15.
 */
public class DBManager {
    private static Context context;

    private static ClothingDbHelper clothingDbHelper;
    private static SQLiteDatabase clothingReadDB, clothingWriteDB;

    public interface OnInitializedListener {
        void onInitialized();
    }

    public static void initDB(Context mContext , final OnInitializedListener listener) {
        context = mContext;

        AsyncTask<Void, Void, Void> initDBTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                clothingDbHelper = new ClothingDbHelper(context);
                clothingReadDB = clothingDbHelper.getReadableDatabase();
                clothingWriteDB = clothingDbHelper.getWritableDatabase();
                listener.onInitialized();
                return null;
            }
        };
        initDBTask.execute();
    }

    public static SQLiteDatabase getReadDB() {
        return clothingReadDB;
    }

    public static SQLiteDatabase getWriteDB() {
        return clothingWriteDB;
    }
}
