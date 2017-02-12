package com.jetruby.dribbble.helper;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;

import static com.jetruby.dribbble.helper.DataForDB.FeedEntry;


/**
 * Created by Dmitry on 05.02.2017.
 */

public class ShotsDBProvider extends ContentProvider  {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "shots.db";


    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    DataForDB.FeedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DataForDB.FeedEntry.TITLE + TEXT_TYPE + COMMA_SEP +
                    DataForDB.FeedEntry.DATE + TEXT_TYPE + COMMA_SEP +
                    DataForDB.FeedEntry.DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    DataForDB.FeedEntry.HIDPI + TEXT_TYPE + COMMA_SEP +
                    DataForDB.FeedEntry.NORMAL + TEXT_TYPE + COMMA_SEP +
                    DataForDB.FeedEntry.TEASER + TEXT_TYPE  +
                    " )";


    DBHelper dbHelper;
    SQLiteDatabase db;
    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(FeedEntry.TABLE_NAME, projection, selection,
                selectionArgs, null, null, sortOrder);
        // просим ContentResolver уведомлять этот курсор
        // об изменениях данных в CONTACT_CONTENT_URI
        cursor.setNotificationUri(getContext().getContentResolver(),
                DataForDB.FeedEntry.CONTENT_URI);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }



    //// UriMatcher
    // общий Uri
    static final int URI_CONTACTS = 1;

    // Uri с указанным ID
    static final int URI_CONTACTS_ID = 2;

    // описание и создание UriMatcher
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FeedEntry.AUTHORITY, FeedEntry.PATH, URI_CONTACTS);
        uriMatcher.addURI(FeedEntry.AUTHORITY, FeedEntry.PATH + "/#", URI_CONTACTS_ID);
    }


    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {


        db = dbHelper.getWritableDatabase();
        long rowID = db.insert(FeedEntry.TABLE_NAME, null, values);
        Uri resultUri = ContentUris.withAppendedId(DataForDB.FeedEntry.CONTENT_URI, rowID);
        // уведомляем ContentResolver, что данные по адресу resultUri изменились
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        db.delete(FeedEntry.TABLE_NAME, null, null);
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }


    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
