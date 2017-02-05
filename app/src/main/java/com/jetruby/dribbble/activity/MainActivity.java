package com.jetruby.dribbble.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

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


public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    private static final String auth = "https://api.dribbble.com/v1/shots?";
    int pageCount = 1;
    String page = "page=" + pageCount;

    int perCount = 50;
    String per = "&per_page=" + perCount;

    private static final String token = "&access_token=b37bed181156700973bdaf9d2ca0d749a04723719f524a27d67303421fbba5b3";
    String url = auth + page + per + token;


    private ArrayList<Shot> shots;
    private ProgressDialog pDialog;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        pDialog = new ProgressDialog(this);
        shots = new ArrayList<>();
        mAdapter = new GalleryAdapter(this, shots);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        if (checkNetwork()) {
            loadJsonFromServer();
        } else {
            loadDataFromDB();
        }
    }
    private void loadDataFromDB(){
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
    public void addDataToDB(Shot shot) {
        ContentValues values = new ContentValues();
        values.put(FeedEntry.TITLE, shot.getTitle());
        values.put(FeedEntry.DATE, shot.getDate());
        values.put(FeedEntry.DESCRIPTION, shot.getDescription());
        values.put(FeedEntry.HIDPI, shot.getHidpi());
        values.put(FeedEntry.NORMAL, shot.getNormal());
        values.put(FeedEntry.TEASER, shot.getTeaser());
        Uri newUri = getContentResolver().insert(FeedEntry.CONTENT_URI, values);

        Log.d("tag", "insert, result Uri : " + newUri.toString());
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

        pDialog.setMessage("Downloading json...");
        pDialog.show();

        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                //Log.d("Debug", response.toString());
                ContentResolver cr = getContentResolver();
                cr.delete(DataForDB.FeedEntry.CONTENT_URI, null, null);
                try {
                    shots.clear();
                    for (int i = 0; i < 50; i++) {

                        JSONObject jShot = (JSONObject) response
                                .get(i);
                        Shot shot = new Shot();
                        shot.setId(jShot.getInt("id"));
                        shot.setTitle(jShot.getString("title"));


                        JSONObject jImages = jShot.getJSONObject("images");
                        //shot.setHidpi((String) jImages.get("hidpi"));
                        shot.setNormal((String) jImages.get("normal"));
                        shot.setTeaser((String) jImages.get("teaser"));
                        shots.add(shot);
                        addDataToDB(shot);
                    }
                    mAdapter.notifyDataSetChanged();
                    try {
                        pDialog.cancel();
                    } catch (Exception e) {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                Log.d("log",response.toString());
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

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
}
