package com.vogella.android.tap4share;

import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import junit.framework.Test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ShakeActivity extends AppCompatActivity {

    private JSONObject jsonObj;
    private ImageData imageData;
    private Boolean shakebool = true;
    private ServerConfig serverconf;
    private SensorManager sensorManager;

    private SensorEventListener listener = new SensorEventListener() {
        //当手机的加速度发生变化时调用 when acceleration changes then use this method
        @Override
        public void onSensorChanged(SensorEvent event) {
            //获取手机在不同方向上加速度的变化 get the changes of acceleration sensor from different directions
            float valuesX = Math.abs(event.values[0]);
            float valuesY = Math.abs(event.values[1]);
            float valuesZ = Math.abs(event.values[2]);

            //感受到摇晃后的操作(可以通过更改数值来调整灵敏度)  What will happen after the shake (We can change the number to modify the sensitive of sensor)
            if ((valuesX > 17 || valuesY > 17 || valuesZ > 17) && shakebool) {
                shakebool = false;
                System.out.println("SHAKE!");
                new GetContacts().execute();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

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

    /**
     *
     *      GET SHAKE DATA FROM ASYNC TASK
     *
     * **/
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            serverconf = new ServerConfig();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(serverconf.getRandom_image_data());

            if (jsonStr != null) {
                try {
                    jsonObj = new JSONObject(jsonStr);

                    String imageid = jsonObj.getString("imageid");
                    String title = jsonObj.getString("title");
                    String description = jsonObj.getString("description");
                    String source = jsonObj.getString("source");
                    String location = "still unknown";
                    try {
                        location = jsonObj.getString("location");
                    }catch (Exception e){

                    }

                    imageData = new ImageData(imageid, source, title, description, location);

                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            /**
             * Updating parsed JSON data into ListView
             * */
            System.out.println("ONPOSTEXECUTE MOTEDE");
            Intent intent = new Intent(ShakeActivity.this,SingleImageInfo.class);
            intent.putExtra("description", imageData.getDescription());
            intent.putExtra("title", imageData.getTitle());
            intent.putExtra("imagefilename", imageData.getSource());
            intent.putExtra("location", imageData.getLocation());

            startActivity(intent);

            finish();

        }

    }

}
