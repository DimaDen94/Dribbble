package com.jetruby.dribbble.activity;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.jetruby.dribbble.R;
import com.jetruby.dribbble.adapter.GalleryAdapter;
import com.jetruby.dribbble.app.AppController;
import com.jetruby.dribbble.helper.DataForDB;
import com.jetruby.dribbble.helper.ShotsDBProvider;
import com.jetruby.dribbble.model.Shot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.jetruby.dribbble.helper.DataForDB.FeedEntry;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String auth = "https://api.dribbble.com/v1/shots?";
    int pageCount = 1;
    String page = "page=" + pageCount;

    int perCount = 50;
    String per = "&per_page=" + perCount;

    private static final String token = "&access_token=b37bed181156700973bdaf9d2ca0d749a04723719f524a27d67303421fbba5b3";
    String url = auth + page + per + token;

    ContentValues values;
    private ArrayList<Shot> shots;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) findViewById(R.id.listView);

        shots = new ArrayList<>();
        mAdapter = new GalleryAdapter(this, shots);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);
        if (checkNetwork()) {
            swipeRefreshLayout.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            swipeRefreshLayout.setRefreshing(true);
                                            loadJsonFromServer();
                                        }
                                    }
            );

        } else {
            loadDataFromDB();
        }
    }

    private void loadDataFromDB() {
        Cursor cursor = getContentResolver().query(FeedEntry.CONTENT_URI, null, null,
                null, null);
        //startManagingCursor(cursor);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int title = cursor.getColumnIndex(FeedEntry.TITLE);
                int date = cursor.getColumnIndex(FeedEntry.DATE);
                int description = cursor.getColumnIndex(FeedEntry.DESCRIPTION);
                int hidpi = cursor.getColumnIndex(FeedEntry.HIDPI);
                int normal = cursor.getColumnIndex(FeedEntry.NORMAL);
                int teaser = cursor.getColumnIndex(FeedEntry.TEASER);
                do {
                    Shot shot = new Shot();
                    shot.setTitle(cursor.getString(title));
                    shot.setDate(cursor.getString(date));
                    shot.setDescription(cursor.getString(description));
                    shot.setHidpi(cursor.getString(hidpi));
                    shot.setNormal(cursor.getString(normal));
                    shot.setTeaser(cursor.getString(teaser));
                    shots.add(shot);

                } while (cursor.moveToNext());
            }
            cursor.close();
        } else

            mAdapter.notifyDataSetChanged();
    }

    private void addShotsToDB() {


    }



    protected boolean checkNetwork() {
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(cs);
        if (cm.getActiveNetworkInfo() == null) {
            return false;
        } else {
            return true;
        }
    }

    private void loadJsonFromServer() {

        swipeRefreshLayout.setRefreshing(true);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                try {
                    ContentResolver cr = getContentResolver();
                    cr.delete(DataForDB.FeedEntry.CONTENT_URI, null, null);
                } catch (Exception e) {
                }

                try {
                    shots.clear();
                    for (int i = 0; i < 50; i++) {

                        JSONObject jShot = (JSONObject) response.get(i);
                        //if animated take next shot
                        if (jShot.getString("animated").equals("true")) {
                            continue;
                        }
                        Shot shot = new Shot();
                        shot.setId(jShot.getInt("id"));
                        shot.setTitle(jShot.getString("title"));
                        shot.setDescription(stripHtml(jShot.getString("description")));


                        JSONObject jImages = jShot.getJSONObject("images");
                        shot.setHidpi(jImages.getString("hidpi"));
                        shot.setNormal(jImages.getString("normal"));
                        shot.setTeaser(jImages.getString("teaser"));

                        shots.add(shot);

                    }
                    mAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                swipeRefreshLayout.setRefreshing(false);
                TheTask savetoDB = new TheTask();

                savetoDB.execute();
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                swipeRefreshLayout.setRefreshing(false);
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        addShotsToDB();
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsObjRequest);
    }

    public String stripHtml(String html) {
        return Html.fromHtml(html).toString();
    }


    @Override
    public void onRefresh() {
        loadJsonFromServer();
    }


    class TheTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            for (int i = 0; i < shots.size(); i++) {
                addDataToDB(shots.get(i));
            }
            return null;
        }
        public void addDataToDB(Shot shot) {
            values = new ContentValues();
            values.put(FeedEntry.TITLE, shot.getTitle());
            values.put(FeedEntry.DATE, shot.getDate());
            values.put(FeedEntry.DESCRIPTION, shot.getDescription());
            values.put(FeedEntry.HIDPI, shot.getHidpi());
            values.put(FeedEntry.NORMAL, shot.getNormal());
            values.put(FeedEntry.TEASER, shot.getTeaser());
            Uri newUri = getContentResolver().insert(FeedEntry.CONTENT_URI, values);
            //TheTask savetoDB = new TheTask();

            //savetoDB.execute(values);
            //Log.d("tag", "insert, result Uri : " + newUri.toString());
        }


    }
}
