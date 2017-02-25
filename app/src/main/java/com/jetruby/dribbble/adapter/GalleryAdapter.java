package com.jetruby.dribbble.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import com.bumptech.glide.request.target.Target;
import com.jetruby.dribbble.R;
import com.jetruby.dribbble.model.Shot;


public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {

    private List<Shot> shots;
    private Context mContext;
    int height;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public TextView title;
        public TextView description;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            title = (TextView) view.findViewById(R.id.name);
            description = (TextView) view.findViewById(R.id.info_text);
        }
    }


    public GalleryAdapter(Context context, List<Shot> shots) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        height = display.getHeight();
        mContext = context;
        this.shots = shots;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_thumbnail, parent, false);

        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) itemView.getLayoutParams();
        if (params.height > height / 2) {
            params.height = height / 2;
        }
        Log.d("log", "height = " + params.height + "   " + height);
        itemView.setLayoutParams(params);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Shot shot = shots.get(position);

        Glide.with(mContext)
                .load(chooseLink(shot))
                .thumbnail(0.01f)
                .crossFade()
                .into(holder.thumbnail);

        holder.title.setText(shot.getTitle());
        if (!shot.getDescription().equals("null"))
            holder.description.setText(shot.getDescription());
        else
            holder.description.setText("");
    }

    @Override
    public int getItemCount() {
        return shots.size();
    }

    private String chooseLink(Shot shot) {
        if (shot.getHidpi() != null && !shot.getHidpi().equals("null"))
            return shot.getHidpi();
        else if (!shot.getNormal().equals("null"))
            return shot.getNormal();
        else return shot.getTeaser();

    }

}