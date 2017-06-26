package com.vogella.android.tap4share;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import junit.framework.Test;

public class ShakeActivity extends AppCompatActivity {

    private SensorEventListener listener = new SensorEventListener() {
        //当手机的加速度发生变化时调用 when acceleration changes then use this method
        @Override
        public void onSensorChanged(SensorEvent event) {
            //获取手机在不同方向上加速度的变化 get the changes of acceleration sensor from different directions
            float valuesX = Math.abs(event.values[0]);
            float valuesY = Math.abs(event.values[1]);
            float valuesZ = Math.abs(event.values[2]);

            //感受到摇晃后的操作(可以通过更改数值来调整灵敏度)  What will happen after the shake (We can change the number to modify the sensitive of sensor)
            if (valuesX > 17 || valuesY > 17 || valuesZ > 17) {
                Intent intent = new Intent(ShakeActivity.this,MainActivity.class);
//                intent.setClass(ShakeActivity.this,MainActivity.class);
                startActivity(intent);

                finish();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_shake);

        initSensor();

    }

    private void initSensor() {
        //获取到一个传感器管理器  get a sensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //获得一个加速度传感器 get a acceleration sensor
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //注册传感器监听， register sensor listening
        //1.监听器
        //2.加速度传感器
        //3.传感器灵敏度
        //传感器灵敏度分为四级，从上往下灵敏度依次降低 There are four sensitive levels of sensor(shown below)
        //SENSOR_DELAY_FASTEST
        //SENSOR_DELAY_GAME
        //SENSOR_DELAY_UI
        //SENSOR_DELAY_NORMAL
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除对加速度传感器的监听  Release the use/listening from the acceleration sensor
        sensorManager.unregisterListener(listener);
    }

}
