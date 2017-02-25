package com.jetruby.dribbble.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.jetruby.dribbble.R;
import com.jetruby.dribbble.adapter.GalleryAdapter;
import com.jetruby.dribbble.app.AppController;
import com.jetruby.dribbble.helper.DataForDB;
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
    private static final String token = "&access_token=b37bed181156700973bdaf9d2ca0d749a04723719f524a27d67303421fbba5b3";

    String pageTag = "pageTag=";
    volatile int pageCount = 1;

    String perTag = "&per_page=";
    int perCount = 50;

    volatile String url = auth + pageTag + pageCount + perTag + perCount + token;

    ContentValues values;
    private ArrayList<Shot> shots;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;


    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    GridLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) findViewById(R.id.listView);

        shots = new ArrayList<>();
        mAdapter = new GalleryAdapter(this, shots);







        mLayoutManager = new GridLayoutManager(getApplicationContext(), 1);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;

                            //Do pagination.. i.e. fetch new data
                            pageCount++;

                            Runnable thread = new Runnable() {
                                @Override
                                public void run() {
                                    loadShotsFromServer2(url);
                                }
                            };
                            thread.run();

                        }
                    }
                }
            }
        });


        swipeRefreshLayout.setOnRefreshListener(this);
        if (checkNetwork()) {
            Runnable thread = new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                    loadShotsFromServer(url);
                }
            };

            swipeRefreshLayout.post(thread);

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

    private void loadJSONFromServer(JSONArray response) throws JSONException {
        for (int i = 0; i < 50; i++) {

            JSONObject jShot = (JSONObject) response.get(i);
            //if animated take next shot
            if (jShot.getString("animated").equals("true")) {
                continue;
            }
            Shot shot = new Shot();
            shot.setId(jShot.getInt("id"));
            shot.setTitle(jShot.getString("title"));
            shot.setDescription(Html.fromHtml(jShot.getString("description")).toString());


            JSONObject jImages = jShot.getJSONObject("images");
            shot.setHidpi(jImages.getString("hidpi"));
            shot.setNormal(jImages.getString("normal"));
            shot.setTeaser(jImages.getString("teaser"));

            shots.add(shot);
            if (shots.size() == 50)
                break;
        }

        /*if (shots.size() < 50) {
            perCount++;
            loadShotsFromServer(auth + pageTag + pageCount + perTag + perCount + token);
        }*/

    }

    private void loadShotsFromServer(String url) {

        swipeRefreshLayout.setRefreshing(true);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                if (shots.size() == perCount) {
                    try {
                        ContentResolver cr = getContentResolver();
                        cr.delete(DataForDB.FeedEntry.CONTENT_URI, null, null);
                    } catch (Exception e) {
                    }
                }

                try {
                    if (shots.size() == 50)
                        shots.clear();

                    loadJSONFromServer(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                    mAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    LoadToDBTask saveToDB = new LoadToDBTask();
                    saveToDB.execute();

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


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsObjRequest);


    }

    private void loadShotsFromServer2(String url) {

        swipeRefreshLayout.setRefreshing(true);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                try {
                    loadJSONFromServer(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                LoadToDBTask saveToDB = new LoadToDBTask();
                saveToDB.execute();

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


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsObjRequest);


    }

    @Override
    public void onRefresh() {
        pageCount = 1;
        loadShotsFromServer(url);
    }

    class LoadToDBTask extends AsyncTask<Void, Void, String> {
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
            getContentResolver().insert(FeedEntry.CONTENT_URI, values);
        }
    }
}
