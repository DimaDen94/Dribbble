package com.jetruby.dribbble.adapter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.jetruby.dribbble.R;
import com.jetruby.dribbble.model.Shot;


public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {

    private List<Shot> shots;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }


    public GalleryAdapter(Context context, List<Shot> shots) {
        mContext = context;
        this.shots = shots;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_thumbnail, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Shot shot = shots.get(position);

        Glide.with(mContext)
                .load(shot.getNormal())

                .crossFade()
                .into(holder.thumbnail);
        /*if (checkNetwork()) {

        } else {
            Glide.with(mContext)
                    .using(new StreamModelLoader<String>() {
                        @Override
                        public DataFetcher<InputStream> getResourceFetcher(final String model, int i, int i1) {
                            return new DataFetcher<InputStream>() {
                                @Override
                                public InputStream loadData(Priority priority) throws Exception {
                                    throw new IOException();
                                }

                                @Override
                                public void cleanup() {

                                }

                                @Override
                                public String getId() {
                                    return model;
                                }

                                @Override
                                public void cancel() {

                                }
                            };
                        }
                    })
                    .load(shot.getNormal())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.thumbnail);
        }
*/

    }

    protected boolean isOnline() {
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager)
                mContext.getSystemService(cs);
        if (cm.getActiveNetworkInfo() == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public int getItemCount() {
        return shots.size();
    }
}