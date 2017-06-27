package com.vogella.android.tap4share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.RecoverySystem;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ImageInsert extends AppCompatActivity {

    String newString;
    String imagename;
    ImageView imageView;
    Bitmap bmp;
    ServerConfig servconfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_insert);
        imageView =(ImageView) findViewById(R.id.photo);
        servconfig = new ServerConfig();


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {
                //method 1 (for bitarray)
                byte[] byteArray = getIntent().getByteArrayExtra("camera_image");

                System.out.println("IMAGENAME: " + extras.getString("camera_image_name"));
                imagename = extras.getString("camera_image_name");

                if (byteArray == null){
                    System.out.println("it iss null!");
                }
                bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//                System.out.println(bmp);
                if (bmp == null){
                    System.out.println("bitmap iss null!");
                }

                System.out.println(imageView);
                imageView.setImageBitmap(bmp);
            }
        } else {
            newString = (String) savedInstanceState.getSerializable("STRING_I_NEED");
        }

        //API POST INSERT
        //Button
        final Button button = (Button) findViewById(R.id.send_post_request);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String imagetitle =  ((EditText) findViewById(R.id.imagetitle)).getText().toString();
                final String imagedescription =  ((EditText) findViewById(R.id.imagedescription)).getText().toString();
                CheckBox locationcheckbox =  ((CheckBox) findViewById(R.id.checkbox_location_enabled));
                final String location;
                if(locationcheckbox.isChecked()){
                    location = "haslocation";
                }else {
                    location = "unknown";
                }

                // Code here executes on main thread after user presses button

                Toast.makeText(getApplicationContext(), "Your image has been shared!", Toast.LENGTH_LONG).show();

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url ="http://"+servconfig.getServerip()+":"+servconfig.getServerport()+"/api/image";


                //SOLUTION 2 STRINGREQUEST
                StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        mView.showMessage(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        mView.showMessage(error.getMessage());
                    }
                }) {
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        //TIMESTAMP
                        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
                        String format = s.format(new Date());

                        HashMap<String, String> params2 = new HashMap<String, String>();
                        params2.put("timestamp", format.toString());
                        params2.put("source", imagename);
                        params2.put("title", imagetitle);
                        params2.put("description", imagedescription);
                        params2.put("location", location);

                        return new JSONObject(params2).toString().getBytes();
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }
                };

                // Add the request to the RequestQueue.
                queue.add(sr);

                Intent back = new Intent();
                back.setClass(ImageInsert.this, MainActivity.class);
                startActivity(back);

            }


        });
    }
}
