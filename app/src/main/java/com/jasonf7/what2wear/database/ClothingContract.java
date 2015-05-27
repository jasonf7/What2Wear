package com.jasonf7.what2wear.database;

import android.provider.BaseColumns;

/**
 * Created by jasonf7 on 26/05/15.
 */
public final class ClothingContract {
    public ClothingContract() {}

    public static abstract class ClothingEntry implements BaseColumns {
        public static final String TABLE_NAME = "clothing";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_PREFERENCE = "preference";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_IMAGE = "image";
    }
}
