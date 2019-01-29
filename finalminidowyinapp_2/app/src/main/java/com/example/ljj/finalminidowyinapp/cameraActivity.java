package com.example.ljj.finalminidowyinapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.telecom.Call;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static com.example.ljj.finalminidowyinapp.utils.Utils.MEDIA_TYPE_IMAGE;
import static com.example.ljj.finalminidowyinapp.utils.Utils.MEDIA_TYPE_VIDEO;
import static com.example.ljj.finalminidowyinapp.utils.Utils.getOutputMediaFile;

public class cameraActivity extends AppCompatActivity implements SurfaceHolder.Callback, SeekBar.OnSeekBarChangeListener {

    private SurfaceView mSurfaceView;
    private Camera mCamera;
    private SeekBar sb;

    private int CAMERA_TYPE = Camera.CameraInfo.CAMERA_FACING_BACK;

    private boolean isRecording = false;

    private int rotationDegree = 0;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);
        mCamera = getCamera(CAMERA_TYPE);
        mSurfaceView = findViewById(R.id.img);
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    mCamera.setPreviewDisplay(holder);
                    mCamera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        });


        if (mCamera.getParameters().isSmoothZoomSupported()){
            Toast.makeText(this,"Zoom:Error!",Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            try {
                sb = findViewById(R.id.sb_zoom);
                sb.setOnSeekBarChangeListener(this);
                Camera.Parameters params = mCamera.getParameters();
                final int MAX = params.getMaxZoom();
                sb.setMax(MAX);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        findViewById(R.id.btn_back1).setOnClickListener(v -> {
            startActivity(new Intent(this,MainActivity.class));
        });


        findViewById(R.id.btn_picture).setOnClickListener(v -> {
            mCamera.takePicture(null, null, mPicture);
            Camera.PictureCallback mPicture = (data, mCamera) -> {
                File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mCamera.startPreview();
            };
        });
        findViewById(R.id.btn_picture).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    v.setBackgroundResource(R.drawable.pic_take_red);
                }else if (event.getAction() == MotionEvent.ACTION_UP){
                    v.setBackgroundResource(R.drawable.pic_take);
                }
                return false;
            }
        });

        findViewById(R.id.btn_record).setOnClickListener(v -> {
            Button bt = findViewById(R.id.btn_record);
            if (isRecording) {
                bt.setBackground(this.getResources().getDrawable(R.drawable.video_take));
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder = null;
                mCamera.lock();
                isRecording = false;
            } else {
                bt.setBackground(this.getResources().getDrawable(R.drawable.video_take_red));
                mMediaRecorder = new MediaRecorder();
                mCamera.unlock();
                mMediaRecorder.setCamera(mCamera);
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
                mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());
                mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
                mMediaRecorder.setOrientationHint(rotationDegree);
                try{
                    mMediaRecorder.prepare();
                    mMediaRecorder.start();
                    isRecording = true;
                }catch (Exception e){
                    releaseMediaRecorder();
                    isRecording = false;
                }
            }
        });

        final int[] cameraPosition = {1};
        findViewById(R.id.btn_facing).setOnClickListener(v -> {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            int cameraCount = Camera.getNumberOfCameras();
            for(int i=0; i<cameraCount; i++){
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraPosition[0] == 1){
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                    mCamera = Camera.open(1);
                    mCamera.setDisplayOrientation(getCameraDisplayOrientation(1));
                    try{
                        mCamera.setPreviewDisplay(surfaceHolder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mCamera.startPreview();
                    cameraPosition[0] = 0;
                    break;
                }
                else{
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                    mCamera = Camera.open(i);
                    mCamera.setDisplayOrientation(getCameraDisplayOrientation(i));
                    try{
                        mCamera.setPreviewDisplay(surfaceHolder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mCamera.startPreview();
                    cameraPosition[0] = 1;
                    break;
                }
            }
        });

    }

    public Camera getCamera(int position) {
        CAMERA_TYPE = position;
        if (mCamera != null) {
            releaseCameraAndPreview();
        }
        Camera cam = Camera.open(position);
        cam.setDisplayOrientation(getCameraDisplayOrientation(position));
        return cam;
    }


    private static final int DEGREE_90 = 90;
    private static final int DEGREE_180 = 180;
    private static final int DEGREE_270 = 270;
    private static final int DEGREE_360 = 360;

    private int getCameraDisplayOrientation(int cameraId) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = DEGREE_90;
                break;
            case Surface.ROTATION_180:
                degrees = DEGREE_180;
                break;
            case Surface.ROTATION_270:
                degrees = DEGREE_270;
                break;
            default:
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % DEGREE_360;
            result = (DEGREE_360 - result) % DEGREE_360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + DEGREE_360) % DEGREE_360;
        }
        return result;
    }


    private void releaseCameraAndPreview() {
        mCamera.release();
    }

    Camera.Size size;

    private void startPreview(SurfaceHolder holder) {
        mCamera.startPreview();
    }


    private MediaRecorder mMediaRecorder;

    private boolean prepareVideoRecorder() {

        return true;
    }


    private void releaseMediaRecorder() {
        mMediaRecorder.release();
        mMediaRecorder = null;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startPreview(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mMediaRecorder.release();
        mCamera.release();
    }


    private Camera.PictureCallback mPicture = (data, camera) -> {
        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (pictureFile == null) {
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
        } catch (IOException e) {
            Log.d("mPicture", "Error accessing file: " + e.getMessage());
        }

        mCamera.startPreview();
    };


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = Math.min(w, h);

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Camera.Parameters params = mCamera.getParameters();
        params.setZoom(progress);
        mCamera.setParameters(params);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
