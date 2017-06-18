package com.vogella.android.tap4share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.util.HashMap;

public class ImageInsert extends AppCompatActivity {

    String newString;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_insert);
        imageView =(ImageView) findViewById(R.id.photo);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {
//                Intent camera_intent = getIntent();
//                newString= extras.getString("camera_image");
//                Bitmap bitcamera = (Bitmap) camera_intent.getParcelableExtra("camera_image");
//                Bitmap bitcamera = extras.getString("camera_image");

                //method 1 (for bitarray)
                byte[] byteArray = getIntent().getByteArrayExtra("camera_image");

                if (byteArray == null){
                    System.out.println("it iss null!");
                }
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//                System.out.println(bmp);
                if (bmp == null){
                    System.out.println("bitmap iss null!");
                }

                //method 2
//                Bitmap bitmap = (Bitmap) getIntent().getParcelableExtra("camera_image");

//                System.out.println("data typen: " + bitcamera.getClass().getName());
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
                final String artistname =  ((EditText) findViewById(R.id.artistname)).getText().toString();
                final String artistbirthplace =  ((EditText) findViewById(R.id.artistbirthplace)).getText().toString();
                final String artistbdate =  ((EditText) findViewById(R.id.artistbdate)).getText().toString();
                final String artistfavorite =  ((EditText) findViewById(R.id.artistfavorite)).getText().toString();

                // Code here executes on main thread after user presses button

                Toast.makeText(getApplicationContext(), "Hi there", Toast.LENGTH_LONG).show();


                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url ="http://141.70.55.157:8080/api/artist";



//SOLUTION 1 STRINGREQUEST
// Request a string response from the provided URL.
//                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                        new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                // Display the first 500 characters of the response string.
////                                mTextView.setText("Response is: "+ response.substring(0,500));
//                            }
//                        }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
////                        mTextView.setText("That didn't work!");
//                    }
//                });

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
                        HashMap<String, String> params2 = new HashMap<String, String>();
                        params2.put("name", artistname);
                        params2.put("birthPlace", artistbirthplace);
                        params2.put("birthDate", artistbdate);
                        params2.put("favoritebool", artistfavorite);
                        return new JSONObject(params2).toString().getBytes();
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }
                };



// Add the request to the RequestQueue.
                queue.add(sr);



            }
        });
    }
}
