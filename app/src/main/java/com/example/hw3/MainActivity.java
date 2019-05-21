package com.example.hw3;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor mSensor;
    private float lastX, lastY, lastZ;
    private Animation ballAnimation;
    private TextView msgTv;
    private ImageView ball;
    private String[] answer_array;
    private int shakecoeff = 0;
    public static final int FADE_DURATION = 1500;
    public static final int START_OFFSET = 1000;
    public static final int THRESHOLD = 240;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ball = findViewById(R.id.ball);
        msgTv = findViewById(R.id.msgTv);
        answer_array = getResources().getStringArray(R.array.answers);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // Success!.
            mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        } else {
            // Failure! No acceleroemter.
        }
        ballAnimation = AnimationUtils.loadAnimation(this, R.anim.animation);
        msgTv.setVisibility(View.INVISIBLE);
        ball.setImageResource(R.drawable.hw3ball_front);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mSensor != null)
            sensorManager.registerListener(this, mSensor, 100000);
        //msgTv.setVisibility(View.INVISIBLE);
        //ball.setImageResource(R.drawable.hw3ball_front);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSensor != null)
            sensorManager.unregisterListener(this, mSensor);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            int a=handleAccelerationSensor(event.values[0], event.values[1], event.values[2]);
            if (a!=0) {
                showAnswer(answer_array, a-1);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public int handleAccelerationSensor(float x, float y, float z) {
        double force = 0d;

        force += Math.pow((x - lastX) / SensorManager.GRAVITY_EARTH, 2.0);
        force += Math.pow((y - lastY) / SensorManager.GRAVITY_EARTH, 2.0);
        force += Math.pow((z - lastZ) / SensorManager.GRAVITY_EARTH, 2.0);

        force = Math.sqrt(force);

        lastX = x;
        lastY = y;
        lastZ = z;

        if (force > ((float) THRESHOLD / 150.0f)) {
            ball.setImageResource(R.drawable.hw3ball_empty);
            ball.startAnimation(ballAnimation);
                lastX = 0;
                lastY = 0;
                lastZ = 0;
                shakecoeff = (int) (abs(x + y + z) % 20)+1;
                return shakecoeff;
            //}
        }
        return 0;
    }

    private void showAnswer(String[] answer_array, int shakecoeff1) {

        msgTv.setText(answer_array[shakecoeff1]);
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setStartOffset(START_OFFSET);
        msgTv.setVisibility(View.VISIBLE);
        animation.setDuration(FADE_DURATION);
        msgTv.startAnimation(animation);
    }
}