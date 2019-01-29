package com.example.ljj.finalminidowyinapp;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ljj.finalminidowyinapp.bean.Feed;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class videoActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {

    private boolean isStopUpdatingProgress = false;
    private MediaPlayer mMediapPlayer;
    private SeekBar mSeekbar;
    private TextView tvCurrentTime;
    private TextView tvTotalTime;
    private Button button;


    private final int NORMAL = 0;

    private final int PLAYING = 1;

    private final int PAUSING = 2;

    private final int STOPING = 3;

    private int currentState = NORMAL;

    private SurfaceHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        mSeekbar = (SeekBar) findViewById(R.id.sb_progress);
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        tvTotalTime = (TextView) findViewById(R.id.tv_total_time);
        mSeekbar.setOnSeekBarChangeListener(this);

        SurfaceView mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        holder = mSurfaceView.getHolder();
        findViewById(R.id.btn_back2).setOnClickListener(v -> {
            startActivity(new Intent(this,MainActivity.class));
        });
        button = findViewById(R.id.btn_start);
    }

    public void start(View v) {
        if (mMediapPlayer != null) {
            if (currentState != PAUSING) {
                mMediapPlayer.start();
                currentState = PLAYING;
                isStopUpdatingProgress = false;
                return;
            } else if (currentState == STOPING) {
                mMediapPlayer.reset();
                mMediapPlayer.release();
            }
        }
        play();
    }

    public void stop(View v) {
        if (mMediapPlayer != null) {
            mMediapPlayer.stop();
        }
    }

    private void play() {
        //todo change position
        Intent intent = getIntent();
        String path = intent.getStringExtra("url");
        mMediapPlayer = new MediaPlayer();
        try {
            mMediapPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediapPlayer.setDisplay(holder);

            mMediapPlayer.setDataSource(path);
            mMediapPlayer.prepare();
            mMediapPlayer.start();

            mMediapPlayer.setOnCompletionListener(this);
            currentState = PLAYING;

            int duration = mMediapPlayer.getDuration();
            mSeekbar.setMax(duration);
            int m = duration / 1000 / 60;
            int s = duration / 1000 % 60;
            tvTotalTime.setText("/" + m + ":" + s);
            tvCurrentTime.setText("00:00");

            isStopUpdatingProgress = false;
            new Thread(new UpdateProgressRunnable()).start();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause(View v) {
        if (mMediapPlayer != null && currentState == PLAYING) {
            mMediapPlayer.pause();
            currentState = PAUSING;
            //停止刷新主线程
            isStopUpdatingProgress = true;
        }
    }

    public void restart(View v) {
        if (mMediapPlayer != null) {
            mMediapPlayer.reset();
            mMediapPlayer.release();
            play();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //当开始拖动时，那么就开始停止刷新线程
        isStopUpdatingProgress = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        //播放器切换到指定的进度位置上
        mMediapPlayer.seekTo(progress);
        isStopUpdatingProgress = false;
        new Thread(new UpdateProgressRunnable()).start();
    }

    class UpdateProgressRunnable implements Runnable {

        @Override
        public void run() {
            //每隔1秒钟取一下当前正在播放的进度，设置给seekbar
            while (!isStopUpdatingProgress) {
                //得到当前进度
                int currentPosition = mMediapPlayer.getCurrentPosition();
                mSeekbar.setProgress(currentPosition);
                final int m = currentPosition / 1000 / 60;
                final int s = currentPosition / 1000 % 60;

                //此方法给定的runable对象，会执行主线程（UI线程中）
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        tvCurrentTime.setText(m + ":" + s);

                    }

                });
                SystemClock.sleep(1000);
            }

        }

    }

}
