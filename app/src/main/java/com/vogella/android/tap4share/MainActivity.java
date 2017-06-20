package com.vogella.android.tap4share;


import android.Manifest;
import android.app.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {


//    API
///???
private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;
    String[] itemname;

    // URL to get contacts JSON
//    private static String url = "http://api.androidhive.info/contacts/";
    private static String url = "http://141.70.55.157:8080/api/images/";

    ArrayList<HashMap<String, String>> contactList;
    ArrayList<ImageData> imagedatalist;



    //other
    private TextView tabHome;
    private TextView tabTap;
    ImageView image;
    private ImageView imageView;


    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_REQUEST = 1888;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

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

        TextView photoButton = (TextView) this.findViewById(R.id.text_tap);
        this.imageView=(ImageView) this.findViewById(R.id.imageView1);

//        tabTap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                tabTap.setSelected(true);
////                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//                }
//            }
//        });

        photoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        System.out.println("HERE IS " + imageView);



        //API
        contactList = new ArrayList<>();
        imagedatalist = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);

        //Get JSON (I think)
        new GetContacts().execute();

    }


    //UI initialize
    private void bindView() {
        tabHome = (TextView) this.findViewById(R.id.text_home);
        tabTap = (TextView) this.findViewById(R.id.text_tap);
        tabHome.setSelected(true);
//        tabHome.setOnClickListener(this);
//        tabTap.setOnClickListener(this);
    }

//    //reset selected text
//    public void selected() {
//        tabHome.setSelected(false);
//        tabTap.setSelected(false);
//    }


//    @Override
//    public void onClick(View v) {
//
//        switch (v.getId()) {
//            case R.id.text_home:
//                selected();
//                tabHome.setSelected(true);
//
//                break;
//
//            case R.id.text_tap:
//                selected();
//                tabTap.setSelected(true);
//
//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//                }
//
//                break;
//
//        }
//    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            image = (ImageView)this.findViewById(R.id.mImageView);
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            image.setImageBitmap(imageBitmap);
//        }
//    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        if (requestCode == CAMERA_REQUEST) {
//            //System.exit(0);
//            Bitmap picture = (Bitmap) data.getExtras().get("data");
//            image.setImageBitmap(picture);
//        }
//    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("here is: " + resultCode);
        if (requestCode == CAMERA_REQUEST && resultCode != 0) {
            //System.exit(0);
            Bitmap picture = (Bitmap) data.getExtras().get("data");
            System.out.println("heeeej: " + picture);
            imageView.setImageBitmap(picture);

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
            //Convert to byte array
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            picture.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byte[] byteArray = stream.toByteArray();
//            ByteArrayOutputStream bs = new ByteArrayOutputStream();
//            picture.compress(Bitmap.CompressFormat.PNG, 50, bs);
//
////            System.out.println("qreeeerefsdfsdfsd");
//            Intent i = new Intent(this, ImageInsert.class);
//            i.putExtra("camera_image", bs.toByteArray());
//            startActivity(i);
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

                // Getting JSON Array node
//                    JSONArray contacts = jsonObj.getJSONArray("contacts");

//                    // looping through All Contacts
                for (int i = 0; i < jsonObj.length(); i++) {

                    JSONObject c = jsonObj.getJSONObject(i);
                    System.out.println("meh count" + c);

                    String id = c.getString("timestamp");
                    String name = c.getString("source");
                    String birthPlace = c.getString("title");
                    String birthDate = c.getString("description");

                    // tmp hash map for single contact
                    HashMap<String, String> contact = new HashMap<>();

                    // adding each child node to HashMap key => value
                    contact.put("id", id);
                    contact.put("name", name);
                    contact.put("birthPlace", birthPlace);
                    contact.put("birthDate", birthDate);
                    ImageData img = new ImageData(id, name, birthPlace, birthDate);
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
        //ADAPTER TEST
        String[] itemname ={
                "Safari",
                "Camera",
                "Global",
                "FireFox",
                "UC Browser",
                "Android Folder",
                "VLC Player",
                "Cold War"
        };

        Integer[] imgid={
                R.drawable.iconround,
                R.drawable.iconsquare,
                R.drawable.iconround,
                R.drawable.iconsquare,
                R.drawable.iconround,
                R.drawable.iconsquare,
                R.drawable.iconround,
                R.drawable.iconsquare,
        };


        CustomListAdapter adapter=new CustomListAdapter(MainActivity.this, R.layout.list_item, imagedatalist);


//        ListAdapter adapter = new SimpleAdapter(
//                MainActivity.this, contactList,
//                R.layout.list_item, new String[]{"id", "name",
//                "birthPlace"}, new int[]{R.id.image,
//                R.id.email, R.id.mobile});

        lv.setAdapter(adapter);
    }

}

    //INSERT IMAGE NR 3 TRY
    private class uploadFileToServerTask extends AsyncTask<String, String, Object> {
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


//                java.net.URL url = new URL((ApplicationConstant.UPLOAD_IMAGE_URL) + IMAGE + customer_id);
//                Log.d(ApplicationConstant.TAG, "url " + url);
                URL url = new URL("http://141.70.55.157:8080/api/uploadfile");
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
//                    Log.d(ApplicationConstant.TAG, "filename " + filename);

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
                    return "true";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "false";
        }

        @Override
        protected void onPostExecute(Object result) {

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
