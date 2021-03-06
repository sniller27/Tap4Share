package com.vogella.android.tap4share;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    //declaration of UI-elements
    private TextView tabHome;
    private ProgressDialog pDialog;
    private ListView lv;

    //declaration of custom instances
    private ServerConfig servconfig;
    private CustomListAdapter adapter;
    private static String url;

    //lists
    private ArrayList<HashMap<String, String>> contactList;
    private ArrayList<ImageData> imagedatalist;

    //final variables
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_REQUEST = 1888;

    private Bitmap picture;

    public MainActivity() {
        servconfig = new ServerConfig();
        url = servconfig.getNewimages();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //permissions
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {//Can add more as per requirement

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                    123);

        }

        //assign UI elements
        tabHome = (TextView) this.findViewById(R.id.text_home);
        final SwipeRefreshLayout mswipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        TextView photoButton = (TextView) this.findViewById(R.id.text_tap);
        TextView shakeButton = (TextView) this.findViewById(R.id.text_shake);
        ListView imagelistview = (ListView) this.findViewById(R.id.list);
        lv = (ListView) findViewById(R.id.list);

        //assign lists
        contactList = new ArrayList<>();
        imagedatalist = new ArrayList<>();

        //set active tab
        tabHome.setSelected(true);

        /**
         * LISTENERS
         * **/

        //refresh view
        mswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                adapter.notifyDataSetChanged();
                new GetContacts().execute();
                mswipeRefreshLayout.setRefreshing(false);
            }
        });

        //camera button
        photoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        //shake button
        shakeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,ShakeActivity.class);
                startActivity(i);
            }
        });

        //listview click
        imagelistview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageData item = (ImageData) adapter.getItem(position);

                Intent intent = new Intent(MainActivity.this,SingleImageInfo.class);

                intent.putExtra("timestamp", item.getTimestamp());
                intent.putExtra("title", item.getTitle());
                intent.putExtra("description", item.getDescription());
                intent.putExtra("imagefilename", item.getSource());
                intent.putExtra("location", item.getLocation());
                startActivity(intent);
            }
        });

        //Get JSON og display listview
        new GetContacts().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode != 0) {

            picture = (Bitmap) data.getExtras().get("data");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            picture.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            File destination = new File(Environment.getExternalStorageDirectory(),"temp.jpg");

            FileOutputStream fo;
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try {
                fo = new FileOutputStream(destination);
                fo.write(byteArray);
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            new uploadFileToServerTask().execute(destination.getAbsolutePath());

        }
    }

    //    private class to get JSON
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONArray jsonObj = new JSONArray(jsonStr);
    //                    // looping through All Contacts
                    for (int i = 0; i < jsonObj.length(); i++) {

                        JSONObject c = jsonObj.getJSONObject(i);

                        String timestamp = c.getString("timestamp");
                        String imagefilename = c.getString("source");
                        String title = c.getString("title");
                        String description = c.getString("description");
                        String location = "just unknown";
                        try {
                            location = c.getString("location");
                        }catch (Exception e){

                        }
                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("timestamp", timestamp);
                        contact.put("imagefilename", imagefilename);
                        contact.put("title", title);
                        contact.put("description", description);
                        contact.put("location", location);
                        ImageData img = new ImageData(timestamp, imagefilename, title, description, location);

                        // adding contact to contact list
                        imagedatalist.add(img);
                        contactList.add(contact);
                    }
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
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            adapter=new CustomListAdapter(MainActivity.this, R.layout.list_item, imagedatalist);

            lv.setAdapter(adapter);
        }
    }

    //INSERT IMAGE NR 3 TRY
    private class uploadFileToServerTask extends AsyncTask<String, String, Object> {
        StringBuilder sb;

        @Override
        protected String doInBackground(String... args) {

            try {
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                @SuppressWarnings("PointlessArithmeticExpression")
                int maxBufferSize = 1 * 1024 * 1024;

                servconfig = new ServerConfig();
                URL url = new URL(servconfig.getUpload_file());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Allow Inputs &amp; Outputs.
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);

                // Set HTTP method to POST.
                connection.setRequestMethod("POST");

                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                FileInputStream fileInputStream;
                DataOutputStream outputStream;
                {
                    outputStream = new DataOutputStream(connection.getOutputStream());

                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    String filename = args[0];

                    outputStream.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + filename + "\"" + lineEnd);
                    outputStream.writeBytes(lineEnd);

                    fileInputStream = new FileInputStream(filename);

                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);

                    buffer = new byte[bufferSize];

                    // Read file
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        outputStream.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }
                    outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                }

                int serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                fileInputStream.close();
                outputStream.flush();
                outputStream.close();

                if (serverResponseCode == 200) {

                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();

                    return sb.toString();

//                    return "true";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "false";
        }

        @Override
        protected void onPostExecute(Object result) {
            //Convert to byte array
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            picture.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            picture.compress(Bitmap.CompressFormat.PNG, 50, bs);
            String sb_intent = sb.substring(1,sb.length()-2);

            Intent i = new Intent(MainActivity.this, ImageInsert.class);
            i.putExtra("camera_image", bs.toByteArray());
            i.putExtra("camera_image_name", sb_intent);
            startActivity(i);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case 123: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)     {
                    //Peform your task here if any
                } else {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {//Can add more as per requirement

                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                                123);

                    } else {

                    }
                }
                return;
            }
        }
    }
}
