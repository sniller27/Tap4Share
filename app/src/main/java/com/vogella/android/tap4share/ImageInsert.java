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
    ImageView imageView;
    Bitmap bmp;

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
                bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
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
                final String artistname =  ((EditText) findViewById(R.id.imagetitle)).getText().toString();
                final String artistbirthplace =  ((EditText) findViewById(R.id.imagedescription)).getText().toString();
//                final String artistbdate =  ((EditText) findViewById(R.id.artistbdate)).getText().toString();
//                final String artistfavorite =  ((EditText) findViewById(R.id.artistfavorite)).getText().toString();

                // Code here executes on main thread after user presses button

                Toast.makeText(getApplicationContext(), "Your image has been shared!", Toast.LENGTH_LONG).show();


                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url ="http://141.70.55.157:8080/api/image";



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
                        //TIMESTAMP
                        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
                        String format = s.format(new Date());

                        HashMap<String, String> params2 = new HashMap<String, String>();
                        params2.put("timestamp", format.toString());
                        params2.put("source", "cat.png");
                        params2.put("title", artistname);
                        params2.put("description", artistbirthplace);

                        return new JSONObject(params2).toString().getBytes();
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }
                };



// Add the request to the RequestQueue.
                queue.add(sr);


//                //INSERT IMAGE BITMAP NR 1 TRY
//                //SOLUTION 2 STRINGREQUEST
//                StringRequest srimage = new StringRequest(Request.Method.POST, "http://141.70.55.157:8080/api/uploadfile", new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
////                        mView.showMessage(response);
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
////                        mView.showMessage(error.getMessage());
//                    }
//                }) {
//                    @Override
//                    public Map<String, String> getParams() throws AuthFailureError {
//
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                        byte[] imageBytes = baos.toByteArray();
//                        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
//
//                        HashMap<String, String> params2 = new HashMap<String, String>();
//                        params2.put("upload", encodedImage);
//
//                        return params2;
//                    }
//
//
//                };
//
//                queue.add(srimage);

            }
        });



    }


//    //INSERT IMAGE NR 2 TRY
//    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
//        @Override
//        protected void onPreExecute() {
//            // setting progress bar to zero
////            progressBar.setProgress(0);
////            super.onPreExecute();
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... progress) {
//            // Making progress bar visible
////            progressBar.setVisibility(View.VISIBLE);
//
//            // updating progress bar value
////            progressBar.setProgress(progress[0]);
//
//            // updating percentage value
////            txtPercentage.setText(String.valueOf(progress[0]) + "%");
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//            return uploadFile();
//        }
//
//        @SuppressWarnings("deprecation")
//        private String uploadFile() {
//            String responseString = null;
//
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpPost httppost = new HttpPost("http://141.70.55.157:8080/api/uploadfile");
//
//            try {
//                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
//                        new RecoverySystem.ProgressListener() {
//
//                            @Override
//                            public void transferred(long num) {
////                                publishProgress((int) ((num / (float) totalSize) * 100));
//                            }
//                        });
//
//                File sourceFile = new File(filePath);
//
//                // Adding file data to http body
//                entity.addPart("image", new FileBody(sourceFile));
//
//                // Extra parameters if you want to pass to server
////                entity.addPart("website",
////                        new StringBody("www.androidhive.info"));
////                entity.addPart("email", new StringBody("abc@gmail.com"));
//
//                totalSize = entity.getContentLength();
//                httppost.setEntity(entity);
//
//                // Making server call
//                HttpResponse response = httpclient.execute(httppost);
//                HttpEntity r_entity = response.getEntity();
//
//                int statusCode = response.getStatusLine().getStatusCode();
//                if (statusCode == 200) {
//                    // Server response
//                    responseString = EntityUtils.toString(r_entity);
//                } else {
//                    responseString = "Error occurred! Http Status Code: "
//                            + statusCode;
//                }
//
//            } catch (Exception e) {
//                responseString = e.toString();
//            }
//
//            return responseString;
//
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
////            Log.e(TAG, "Response from server: " + result);
//
//            // showing the server response in an alert dialog
////            showAlert(result);
//
//            super.onPostExecute(result);
//        }
//
//    }



//    //INSERT IMAGE NR 3 TRY
//    private class uploadFileToServerTask extends AsyncTask<String, String, Object> {
//        @Override
//        protected String doInBackground(String... args) {
//            try {
//                String lineEnd = "\r\n";
//                String twoHyphens = "--";
//                String boundary = "*****";
//                int bytesRead, bytesAvailable, bufferSize;
//                byte[] buffer;
//                @SuppressWarnings("PointlessArithmeticExpression")
//                int maxBufferSize = 1 * 1024 * 1024;
//
//
////                java.net.URL url = new URL((ApplicationConstant.UPLOAD_IMAGE_URL) + IMAGE + customer_id);
////                Log.d(ApplicationConstant.TAG, "url " + url);
//                URL url = new URL("http://141.70.55.157:8080/api/uploadfile");
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//                // Allow Inputs &amp; Outputs.
//                connection.setDoInput(true);
//                connection.setDoOutput(true);
//                connection.setUseCaches(false);
//
//                // Set HTTP method to POST.
//                connection.setRequestMethod("POST");
//
//                connection.setRequestProperty("Connection", "Keep-Alive");
//                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//
//                FileInputStream fileInputStream;
//                DataOutputStream outputStream;
//                {
//                    outputStream = new DataOutputStream(connection.getOutputStream());
//
//                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);
//                    String filename = args[0];
//                    outputStream.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + filename + "\"" + lineEnd);
//                    outputStream.writeBytes(lineEnd);
////                    Log.d(ApplicationConstant.TAG, "filename " + filename);
//
//                    fileInputStream = new FileInputStream(filename);
//
//                    bytesAvailable = fileInputStream.available();
//                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
//
//                    buffer = new byte[bufferSize];
//
//                    // Read file
//                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
//                    while (bytesRead > 0) {
//                        outputStream.write(buffer, 0, bufferSize);
//                        bytesAvailable = fileInputStream.available();
//                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//                    }
//                    outputStream.writeBytes(lineEnd);
//                    outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//                }
//
//                int serverResponseCode = connection.getResponseCode();
//                String serverResponseMessage = connection.getResponseMessage();
//                Log.d("serverResponseCode", "" + serverResponseCode);
//                Log.d("serverResponseMessage", "" + serverResponseMessage);
//
//                fileInputStream.close();
//                outputStream.flush();
//                outputStream.close();
//
//                if (serverResponseCode == 200) {
//                    return "true";
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return "false";
//        }
//
//        @Override
//        protected void onPostExecute(Object result) {
//
//        }
//    }
}
