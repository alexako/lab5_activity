package com.alexjreyes.lab5_reyes_a;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;
    private Sensor proxSensor;

    private static final int SENSOR_SENSITIVITY = 4;

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        proxSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (sensor != null) {
            sensorManager.registerListener(this,
                    sensor,
                    SensorManager.SENSOR_DELAY_FASTEST);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    triggerEvent();
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }

        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] >= -SENSOR_SENSITIVITY && event.values[0] <= SENSOR_SENSITIVITY) {
                Toast.makeText(getApplicationContext(), "Ahhhhh!", Toast.LENGTH_SHORT).show();
                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), getRandomScream());
                mp.start();
                triggerVibration();
            }
        }
    }


    private void triggerEvent() {
        TextView t = findViewById(R.id.textView);
        t.setText(getRandomActionWord());

        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), getRandomSound());
        mp.start();
        triggerVibration();
    }

    private void triggerVibration() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(500);
        }
    }

    private int getRandomScream() {
        int[] screams = {
                R.raw.sc1,
                R.raw.sc2,
                R.raw.sc3,
        };

        Random r = new Random();
        int Low = 0;
        int High = screams.length;
        int rndm = r.nextInt(High-Low) + Low;

        return screams[rndm];
    }

    private int getRandomSound() {
        int[] sounds = {
                R.raw.s0,
                R.raw.s1,
                R.raw.s2,
                R.raw.s3,
                R.raw.s4,
                R.raw.s5,
                R.raw.s6,
                R.raw.s7,
                R.raw.s8,
                R.raw.s9
        };

        Random r = new Random();
        int Low = 0;
        int High = sounds.length;
        int rndm = r.nextInt(High-Low) + Low;

        return sounds[rndm];
    }

    private String getRandomActionWord() {
        String[] words = {
                "bang",
                "blab",
                "boink",
                "bonk",
                "boom",
                "brring",
                "clash",
                "clank",
                "clink",
                "clack",
                "ding",
                "bing",
                "ker-ching",
                "ker-plunk",
                "ouch",
                "ping",
                "plog",
                "poof",
                "pong",
                "pop",
                "swish",
                "swoosh",
                "thwack",
                "whack",
                "whoosh",
                "wham",
                "zing"
        };


        Random r = new Random();
        int Low = 0;
        int High = words.length;
        int rndm = r.nextInt(High-Low) + Low;

        return words[rndm] + "!!!";
    }
}
