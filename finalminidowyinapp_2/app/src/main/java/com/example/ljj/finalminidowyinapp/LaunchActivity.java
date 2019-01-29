package com.example.ljj.finalminidowyinapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LaunchActivity extends AppCompatActivity {

    private  final int SPLASH_DISPLAY_LENGHT = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(LaunchActivity.this,MainActivity.class);
                LaunchActivity.this.startActivity(mainIntent);
                LaunchActivity.this.finish();
            }
        },SPLASH_DISPLAY_LENGHT);
    }
}
