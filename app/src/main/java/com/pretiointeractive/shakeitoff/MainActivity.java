package com.pretiointeractive.shakeitoff;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    private Context mContext;
    private RelativeLayout currentLayout;
    private PlayMusic mp;
    private static int score = 0;
    private static int highScore;
    private static boolean stillShaking = false;
    private static Handler handler;
    private long lastTimestamp;
    private boolean gameOver = false;

    private Timer timer;
    private TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentLayout = (RelativeLayout) findViewById(R.id.mainLayout);

        mContext = this;

        mp = new PlayMusic(mContext);
        mp.setOnCompleteListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopMusic();
            }
        });

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count, long timestamp) {
                stillShaking = true;
                lastTimestamp = timestamp;
                handlePlayback(count);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        if(mp != null && mp.isPlaying()){
            stopMusic();
        }
        super.onPause();
    }

    private void handlePlayback(int count){
        Log.d("Score", count + "");
        if (count != 0) {
            score = count;
            playMusic();
        }
        else{
            stopMusic();
        }
    }

    private void playMusic(){
        if (mp.isPlaying()){
            return;
        }

        handler = new Handler();
        if (timerTask == null){
            timer = new Timer();
            timerTask = new TimerTask() {
                public void run() {
                    handler.post(new Runnable() {
                        public void run(){
                            stillShaking = isStillShaking();
                            if(!stillShaking && !gameOver){
                                stopMusic();
                            }
                        }
                    });
                }
            };
            timer.schedule(timerTask, 0, 3000);
        }

        currentLayout.setBackgroundResource(R.drawable.michael_no);
        gameOver = false;
        mp.doInBackground();
    }

    private void stopMusic(){
        if (mp.isPlaying()){
            mp.stop();
        }
        showScore();
        currentLayout.setBackgroundResource(R.drawable.michael_smile);
    }

    private void showScore(){

        stillShaking = isStillShaking();
        if (stillShaking){
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        String message = "Your Score is: ";
        if (score > highScore){
            message = "Congratulations! You set a new high score: ";
            highScore = score;
        }
        builder.setNeutralButton(message + score, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        gameOver();
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void gameOver(){
        gameOver = true;
        timerTask.cancel();
        timer.cancel();
        timer.purge();
        timer = null;
        timerTask = null;
        mp = new PlayMusic(mContext);

    }

    private boolean isStillShaking(){
        long currentTimeMillis = System.currentTimeMillis();
        long difference = currentTimeMillis - lastTimestamp;
        if (difference >= 1000){
            return false;
        }
        return true;
    }
}
