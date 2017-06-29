package com.vogella.android.tap4share;


import android.Manifest;
import android.app.Activity;

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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.util.Objects;


public class MainActivity extends AppCompatActivity {


//    API
///???
private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;
    ServerConfig servconfig;
    CustomListAdapter adapter;

    // URL to get contacts JSON
    private static String url;

    public MainActivity() {
        servconfig = new ServerConfig();
        url = servconfig.getAll_images();
    }

    ArrayList<HashMap<String, String>> contactList;
    ArrayList<ImageData> imagedatalist;



    //other
    private TextView tabHome;
    private TextView tabTap;
    ImageView image;
    private ImageView imageView;


    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_REQUEST = 1888;
    Bitmap picture;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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

        bindView();

        /**
         * LISTENERS
         * **/

        TextView photoButton = (TextView) this.findViewById(R.id.text_tap);

        //camera button
        photoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        //shake button
        TextView shakeButton = (TextView) this.findViewById(R.id.text_shake);

        shakeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,ShakeActivity.class);
                startActivity(i);
            }
        });

        //listview click
        ListView imagelistview = (ListView) this.findViewById(R.id.list);

        imagelistview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                System.out.println("LISTENER1: " + parent.getItemAtPosition(position));
//                System.out.println("POSITION: " + position);
//                System.out.println("VIEW: " + view);
//                System.out.println("ID: " + id);
//
//                Toast.makeText(getApplicationContext(), "Your image has been shared!", Toast.LENGTH_LONG).show();
//
//
//                Intent intent = new Intent(MainActivity.this,SingleImageInfo.class);
//                //based on item add info to intent
//                startActivity(intent);




////                ARRAYADAPTER ADD
//                ImageData ko = new ImageData("15", "adsssd", "asss", "ddd");
//                imagedatalist.add(ko);
//
////                final ArrayAdapter adapter1 = ((ArrayAdapter)getListAdapter());
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
////                        mInfo.setText(str);
//                        adapter.notifyDataSetChanged();
//                    }
//                });

                ImageData item = (ImageData) adapter.getItem(position);
//                System.out.println("HARSSS ITEM: " + item.getTimestamp());

                Intent intent = new Intent(MainActivity.this,SingleImageInfo.class);
                //based on item add info to intent
//                intent.putExtra("id", item.getTimestamp());

//              BITMAP TO BYTEARRAY
//                ByteArrayOutputStream bs = new ByteArrayOutputStream();
//                picture.compress(Bitmap.CompressFormat.PNG, 50, bs);
//                intent.putExtra("description", bs.toByteArray());

                intent.putExtra("timestamp", item.getTimestamp());
                intent.putExtra("title", item.getTitle());
                intent.putExtra("description", item.getDescription());
                intent.putExtra("imagefilename", item.getSource());
                intent.putExtra("location", item.getLocation());
                startActivity(intent);

            }
        });


        //API
//        contactList = new ArrayList<>();
//        imagedatalist = new ArrayList<>();
//
//        lv = (ListView) findViewById(R.id.list);
//
//        //Get JSON (I think)
//        new GetContacts().execute();

    }

    @Override
    protected void onResume() {
        super.onResume();

        //API
        contactList = new ArrayList<>();
        imagedatalist = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);

        //Get JSON (I think)
        new GetContacts().execute();
        System.out.println("RESUME GOES!!!");

    }

    //UI initialize
    private void bindView() {
        tabHome = (TextView) this.findViewById(R.id.text_home);
        tabTap = (TextView) this.findViewById(R.id.text_tap);
        tabHome.setSelected(true);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("here is: " + resultCode);
        if (requestCode == CAMERA_REQUEST && resultCode != 0) {
            //System.exit(0);
            picture = (Bitmap) data.getExtras().get("data");
            System.out.println("heeeej: " + picture);

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

            System.out.println("SE HER mappe: " + Environment.getExternalStorageDirectory());
            System.out.println("SE HER fil: " + destination);

            new uploadFileToServerTask().execute(destination.getAbsolutePath());

        }
    }




//    API CLASS
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

        Log.e(TAG, "Response from url: " + jsonStr);

        if (jsonStr != null) {
            try {
                JSONArray jsonObj = new JSONArray(jsonStr);

//                    // looping through All Contacts
                for (int i = 0; i < jsonObj.length(); i++) {

                    JSONObject c = jsonObj.getJSONObject(i);
                    System.out.println("meh count" + c);

                    String timestamp = c.getString("timestamp");
                    String imagefilename = c.getString("source");
                    String title = c.getString("title");
                    String description = c.getString("description");
                    String location = "just unknown";
                    try {
                        location = c.getString("location");
                    }catch (Exception e){

                    }
System.out.println("ATTIRBTES: " + timestamp + " and " + imagefilename + " and " + title + " and " + description);
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
                Log.e(TAG, "Json parsing error: " + e.getMessage());
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
            Log.e(TAG, "Couldn't get json from server.");
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
        System.out.println("postexecute");
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

                URL url = new URL("http://"+servconfig.getServerip()+":"+servconfig.getServerport()+"/api/uploadfile");
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
                Log.d("serverResponseCode", "" + serverResponseCode);
                Log.d("serverResponseMessage", "" + serverResponseMessage);

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
