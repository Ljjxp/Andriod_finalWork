package com.example.ljj.finalminidowyinapp.Adapter;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.ljj.finalminidowyinapp.bean.Feed;

import java.util.List;

public class easyAdapter extends RecyclerView.Adapter<easyAdapter.myViewHolder>{

    public ListItemClickListener mOnClickListener;

    private List<Feed> mFeeds;

    public easyAdapter(ListItemClickListener listener, List<Feed> Feeds) {
        mOnClickListener = listener;
        mFeeds = Feeds;
    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ImageView imageView = new ImageView(viewGroup.getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setAdjustViewBounds(true);
        myViewHolder viewHolder = new myViewHolder(imageView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder myViewHolder, int i) {
        ImageView iv = (ImageView) myViewHolder.itemView;
        Glide.with(iv.getContext()).load(mFeeds.get(i).getImage_url()).into(iv);
    }


    @Override
    public int getItemCount() {
        return mFeeds.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder/* implements View.OnClickListener */{

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            //itemView.setOnClickListener(this);
        }

/*        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            if (mOnClickListener != null) {
                mOnClickListener.onListItemClick(clickedPosition);
            }
        }*/
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }
}
