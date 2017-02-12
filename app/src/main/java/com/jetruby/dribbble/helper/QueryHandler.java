package com.jetruby.dribbble.helper;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by Dmitry on 12.02.2017.
 */

public class QueryHandler extends AsyncQueryHandler {
    public QueryHandler(ContentResolver cr) {
        super(cr);
    }
    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        // query() completed
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        // insert() completed
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        // update() completed
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        // delete() completed
    }
}
