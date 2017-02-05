package com.jetruby.dribbble.helper;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Dmitry on 05.02.2017.
 */

public final class DataForDB {
    public DataForDB() {}

    /* Inner class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {
        public static final String _ID = "id";
        public static final String TABLE_NAME = "shots";
        public static final String TITLE = "title";
        public static final String DATE = "date";
        public static final String DESCRIPTION = "description";
        public static final String HIDPI = "hidpi";
        public static final String NORMAL = "normal";
        public static final String TEASER = "teaser";



        // authority
        public static final String AUTHORITY = "com.jetruby.dribble";

        // path
        public static final String PATH = "shots";

        //Uri
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/" + PATH);
    }
}
