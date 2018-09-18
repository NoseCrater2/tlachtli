package com.example.angel.tlactli;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity  {

    Bola bolaView = null;
    Handler RedrawHandlar = new Handler();
    Timer mTmr = null;
    TimerTask mTsk = null;
    int mScrWidth,mScrHeight;

    android.graphics.PointF mBallPos,mBallSpd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(0xFFFFFFFF, WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FrameLayout mainView = (android.widget.FrameLayout)findViewById(R.id.main_view);
        Display display = getWindowManager().getDefaultDisplay();
        mScrWidth = display.getWidth();
        mScrHeight = display.getHeight();
        mBallPos = new android.graphics.PointF();
        mBallSpd = new android.graphics.PointF();


        mBallPos.x = mScrWidth/2;
        mBallPos.y = mScrHeight/2;
        mBallSpd.x = 0;
        mBallSpd.y = 0;


        bolaView = new Bola(this,mBallPos.x,mBallPos.y,20);

        mainView.addView(bolaView);
        bolaView.invalidate();

        ((SensorManager)getSystemService(Context.SENSOR_SERVICE)).registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                mBallSpd.x = -sensorEvent.values[0];
                mBallSpd.y = sensorEvent.values[1];
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {}
        },((SensorManager)getSystemService(Context.SENSOR_SERVICE)).getSensorList(Sensor.TYPE_ACCELEROMETER).get(0),SensorManager.SENSOR_DELAY_NORMAL);


        mainView.setOnTouchListener(new android.view.View.OnTouchListener(){
            public boolean onTouch(android.view.View v,android.view.MotionEvent e){
                mBallPos.x = e.getX();
                mBallPos.y = e.getY();
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       menu.add("Exit");
       return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getTitle() == "Exit")finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        mTmr.cancel();
        mTmr = null;
        mTsk = null;
        super.onPause();
    }

    @Override
    public void onResume()
    {

        mTmr = new Timer();
        mTsk = new TimerTask() {
            public void run() {

                mBallPos.x += mBallSpd.x;
                mBallPos.y += mBallSpd.y;

                if (mBallPos.x > mScrWidth) mBallPos.x=mScrWidth-20;
                if (mBallPos.y > mScrHeight) mBallPos.y=mScrHeight-20;
                if (mBallPos.x < 0) mBallPos.x=0+20;
                if (mBallPos.y < 0) mBallPos.y=0+20;

                bolaView.mX = mBallPos.x;
                bolaView.mY = mBallPos.y;



                RedrawHandlar.post(new Runnable() {
                    public void run() {
                        bolaView.invalidate();
                    }});
            }}; // TimerTask

        mTmr.schedule(mTsk,10,10);
        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        System.runFinalizersOnExit(true);
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }

}
