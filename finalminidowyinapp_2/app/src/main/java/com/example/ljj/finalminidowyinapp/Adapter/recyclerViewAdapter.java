package com.example.ljj.finalminidowyinapp.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ljj.finalminidowyinapp.MainActivity;
import com.example.ljj.finalminidowyinapp.R;
import com.example.ljj.finalminidowyinapp.bean.Feed;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class recyclerViewAdapter extends RecyclerView.Adapter<recyclerViewAdapter.myViewHolder>{

    private final ListItemClickListener mOnClickListener;

    private List<Feed> mFeeds;


    public recyclerViewAdapter(ListItemClickListener listener, List<Feed> Feeds) {
        mOnClickListener = listener;
        mFeeds = Feeds;
    }

    @NonNull
    @Override
    public recyclerViewAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.activity_myadapter;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        myViewHolder viewHolder = new myViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder myViewHolder, int position) {
        Feed feed = mFeeds.get(position);
        myViewHolder.updateUI(feed);
    }


    @Override
    public int getItemCount() {
        return mFeeds.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView zz;
        private final ImageView myImage;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            zz = itemView.findViewById(R.id.img);
            myImage = itemView.findViewById(R.id.zz);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            if (mOnClickListener != null) {
                mOnClickListener.onListItemClick(clickedPosition);
            }
        }

        public void updateUI(Feed feed){
            zz.setText(feed.getUserName());
            Bitmap bt = getBitmap(feed.getImage_url());
            myImage.setImageBitmap(bt);
        }
    }

    public Bitmap getBitmap(String urlpath) {
        Bitmap map = null;
        try {
            URL url = new URL(urlpath);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream in;
            in = conn.getInputStream();
            map = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }
}
