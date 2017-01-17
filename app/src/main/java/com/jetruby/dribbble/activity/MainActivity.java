package com.jetruby.dribbble.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jetruby.dribbble.R;
import com.jetruby.dribbble.adapter.GalleryAdapter;
import com.jetruby.dribbble.app.AppController;
import com.jetruby.dribbble.model.Shot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private static final String auth = "https://api.dribbble.com/v1/shots?access_token=";
    private static final String token = "b37bed181156700973bdaf9d2ca0d749a04723719f524a27d67303421fbba5b3";
    private static final String url = auth + token;
    private ArrayList<Shot> shots;
    private ProgressDialog pDialog;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        pDialog = new ProgressDialog(this);
        shots = new ArrayList<>();
        mAdapter = new GalleryAdapter(this, shots);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        fetchImages();
    }

    private void fetchImages() {

        pDialog.setMessage("Downloading json...");
        pDialog.show();



        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                //Log.d("Debug", response.toString());
                try {
                    shots.clear();
                    for (int i = 0; i < 10; i++) {

                        JSONObject jShot = (JSONObject) response
                                .get(i);
                        Shot shot = new Shot();
                        shot.setId(jShot.getInt("id"));
                        shot.setTitle(jShot.getString("title"));


                        JSONObject jImages = jShot.getJSONObject("images");
                        shot.setHidpi((String) jImages.get("hidpi"));
                        shot.setNormal((String) jImages.get("normal"));
                        shot.setTeaser((String) jImages.get("teaser"));
                        shots.add(shot);
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

            }
        }){

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
