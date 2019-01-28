package com.example.ljj.finalminidowyinapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;

import com.bumptech.glide.Glide;
import com.example.ljj.finalminidowyinapp.bean.Feed;
import com.example.ljj.finalminidowyinapp.bean.FeedResponse;
import com.example.ljj.finalminidowyinapp.bean.PostVideoResponse;
import com.example.ljj.finalminidowyinapp.newtork.IMiniDouyinService;
import com.example.ljj.finalminidowyinapp.newtork.RetrofitManager;
import com.example.ljj.finalminidowyinapp.utils.ResourceUtils;
import com.example.ljj.finalminidowyinapp.OnClick.RecyclerViewClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RecyclerViewClickListener.OnItem2ClickListener{

    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;
    private RecyclerView mRv;
    private List<Feed> mFeeds = new ArrayList<>();
    public Uri mSelectedImage;
    private Uri mSelectedVideo;
    public Button mBtn;
    private Button mBtnRefresh;


    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    final int REQUEST_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initRecyclerView();
        initButtonSelect();
        findViewById(R.id.btn_film).setOnClickListener(v -> {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            startActivity(new Intent(MainActivity.this, cameraActivity.class));
        });
    }

    private void initButtonSelect() {
        mBtn = findViewById(R.id.btn);
        mBtn.setOnClickListener(v -> {
            String s = mBtn.getText().toString();
            if (getString(R.string.select_an_image).equals(s)) {
                chooseImage();
            } else if (getString(R.string.select_a_video).equals(s)) {
                chooseVideo();
            } else if (getString(R.string.post_it).equals(s)) {
                if (mSelectedVideo != null && mSelectedImage != null) {
                    postVideo();
                } else {
                    throw new IllegalArgumentException("error data uri, mSelectedVideo = " + mSelectedVideo + ", mSelectedImage = " + mSelectedImage);
                }
            } else if ((getString(R.string.success_try_refresh).equals(s))) {
                mBtn.setText(R.string.select_an_image);
                mBtn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.picture));
            }
        });
        mBtnRefresh = findViewById(R.id.btn_refresh);
    }

    private void initRecyclerView() {
        mRv = findViewById(R.id.rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRv.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        mRv.setHasFixedSize(true);
        mRv.setAdapter(new RecyclerView.Adapter() {
            @NonNull @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                Context context = viewGroup.getContext();
                int layoutIdForListItem = R.layout.activity_myadapter;
                boolean shouldAttachToParentImmediately = false;
                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
                return new MyViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                ImageView iv = (ImageView) viewHolder.itemView.findViewById(R.id.pic_url);
                Glide.with(iv.getContext()).load(mFeeds.get(i).getImage_url()).into(iv);
                TextView tx = viewHolder.itemView.findViewById(R.id.zz_name_2);
                tx.setText("@ " + mFeeds.get(i).getUserName());
            }

            @Override public int getItemCount() {
                return mFeeds.size();
            }
        });

        mRv.addOnItemTouchListener(new RecyclerViewClickListener(this, mRv, this));

    }

    @Override
    public void onItemClick(View view, int position) {
        ImageView iv = (ImageView) view.findViewById(R.id.heart);
        iv.setImageDrawable(this.getResources().getDrawable(R.drawable.heart_red));
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, videoActivity.class);
        intent.putExtra("url",mFeeds.get(position).getVideo_url());
        startActivity(intent);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE);
    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"),
                PICK_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && null != data) {

            if (requestCode == PICK_IMAGE) {
                mSelectedImage = data.getData();
                mBtn.setText(R.string.select_a_video);
                mBtn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.video));
            } else if (requestCode == PICK_VIDEO) {
                mSelectedVideo = data.getData();
                mBtn.setText(R.string.post_it);
                mBtn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.post));
            }
        }
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        File f = new File(ResourceUtils.getRealPath(this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    private void postVideo() {
        mBtn.setText("POSTING...");
        mBtn.setBackgroundDrawable(MainActivity.this.getResources().getDrawable(R.drawable.is_waiting));
        mBtn.setEnabled(false);
        RetrofitManager.get(IMiniDouyinService.HOST).create(IMiniDouyinService.class).createVideo("1120171615", "jj", getMultipartFromUri("cover_image", mSelectedImage), getMultipartFromUri("video", mSelectedVideo)).enqueue(new Callback<PostVideoResponse>() {
            @Override
            public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response) {
                String toast;
                if (response.isSuccessful()) {
                    toast = "Post Success!";
                    mBtn.setText(R.string.success_try_refresh);
                    mBtn.setBackgroundDrawable(MainActivity.this.getResources().getDrawable(R.drawable.gou));
                } else {
                    toast = "Post Failure... Please try again!";
                    mBtn.setText(R.string.post_it);
                    mBtn.setBackgroundDrawable(MainActivity.this.getResources().getDrawable(R.drawable.iscross));
                }
                Toast.makeText(MainActivity.this, toast, Toast.LENGTH_LONG).show();
                mBtn.setEnabled(true);
            }

            @Override public void onFailure(Call<PostVideoResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                mBtn.setText(R.string.post_it);
                mBtn.setBackgroundDrawable(MainActivity.this.getResources().getDrawable(R.drawable.iscross));
                mBtn.setEnabled(true);
            }
        });
    }

    public void fetchFeed(View view) {
        mBtnRefresh.setText("requesting...");
        mBtnRefresh.setBackgroundDrawable(MainActivity.this.getResources().getDrawable(R.drawable.is_waiting));
        mBtnRefresh.setEnabled(false);
        RetrofitManager.get(IMiniDouyinService.HOST).create(IMiniDouyinService.class).fetchFeed().enqueue(new Callback<FeedResponse>() {
            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> response) {
                if (response.isSuccessful()) {
                    mFeeds = response.body().getFeeds();
                    mRv.getAdapter().notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "fetch feed failure!", Toast.LENGTH_LONG).show();
                    mBtnRefresh.setBackgroundDrawable(MainActivity.this.getResources().getDrawable(R.drawable.iscross));
                }
                resetBtn();
            }

            @Override public void onFailure(Call<FeedResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                resetBtn();
            }

            private void resetBtn() {
                mBtnRefresh.setText(R.string.refresh_feed);
                mBtnRefresh.setBackgroundDrawable(MainActivity.this.getResources().getDrawable(R.drawable.refresh));
                mBtnRefresh.setEnabled(true);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mBtnRefresh.performClick();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBtnRefresh.performClick();
    }

}
