package com.vogella.android.tap4share;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Looper;
import android.os.RecoverySystem;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    String Address;
    String location;
    String currentlocation = "unknown";

    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    private boolean check;
    private Location mCurrentLocation;
    private SettingsClient mSettingsClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean mRequestingLocationUpdates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_insert);
        imageView =(ImageView) findViewById(R.id.photo);

        mRequestingLocationUpdates = false;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        updateValuesFromBundle(savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();

        StrictMode.setThreadPolicy(new
                StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(
                new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.INTERNET,android.Manifest.permission.INTERNET}, 123);
        }



        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {
                //method 1 (for bitarray)
                byte[] byteArray = getIntent().getByteArrayExtra("camera_image");

                imagename = extras.getString("camera_image_name");

                if (byteArray == null){
                    System.out.println("it iss null!");
                }
                bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//                System.out.println(bmp);
                if (bmp == null){
                    System.out.println("bitmap iss null!");
                }

                imageView.setImageBitmap(bmp);
            }
        } else {
            newString = (String) savedInstanceState.getSerializable("STRING_I_NEED");
        }


        CheckBox locationcheckbox =  ((CheckBox) findViewById(R.id.checkbox_location_enabled));
        locationcheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startLocationUpdates();
                    stopLocationUpdates();
//                    location = Address;
                }
                else
                {
                    currentlocation = "unknown";
                }
            }
        });


        //API POST INSERT
        //Button
        final Button button = (Button) findViewById(R.id.send_post_request);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final EditText imagetitle_box = (EditText) findViewById(R.id.imagetitle);
                final String imagetitle =  imagetitle_box.getText().toString();

                final EditText imagedescription_box = (EditText) findViewById(R.id.imagedescription);
                final String imagedescription =  imagedescription_box.getText().toString();


                if (imagetitle.replace(" ","").length() == 0){
                    imagetitle_box.requestFocus();
                    imagetitle_box.setError("Fill in image title");
                }else if(imagedescription.replace(" ","").length() == 0){
                    imagedescription_box.requestFocus();
                    imagedescription_box.setError("Fill in image description");
                }else {

                // Code here executes on main thread after user presses button

                Toast.makeText(getApplicationContext(), "Your image has been shared!", Toast.LENGTH_LONG).show();

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    servconfig = new ServerConfig();
                    String url = servconfig.getSingle_image_information_by_title();


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
                        params2.put("location", currentlocation);

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
                back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(back);

                    //kill activity off the stack, so back button won't work
                    finish();
                }


            }


        });

    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(KEY_REQUESTING_LOCATION_UPDATES);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }

        }
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
            }
        };
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void startLocationUpdates() {

        if (!check)
        {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION}, 123);
            }
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        mCurrentLocation=location;
                    }
                }
            });
            check = true;
        }

        // Begin by checking if the device has the necessary location settings.

        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                        if (mCurrentLocation != null)
                        {
                            String add = "";
//                            show_location = (TextView)MainActivity.this.findViewById(R.id.show_location);
//                            show_address=(TextView)MainActivity.this.findViewById(R.id.show_address);
//                            //先纬度Latitude 后经度Longitude
////                            Toast.makeText(getApplicationContext(),"Location: " + mCurrentLocation.getLatitude() + " , "+ mCurrentLocation.getLongitude() ,Toast.LENGTH_LONG).show();
//                            show_location.setText("Location:\n" + "Latitude: " + mCurrentLocation.getLatitude() + "   Longitude: "+ mCurrentLocation.getLongitude());

                            try {
                                getAddress(mCurrentLocation);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                            show_address.setText("Address:\n" + add);
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Toast.makeText(getApplicationContext(),"Location settings are not satisfied. Attempting to upgrade location settings" ,Toast.LENGTH_LONG).show();
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(ImageInsert.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Toast.makeText(getApplicationContext(),"PendingIntent unable to execute request.",Toast.LENGTH_LONG).show();
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be fixed here. Fix in Settings.";
                                Toast.makeText(ImageInsert.this, errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }

                    }
                });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Toast.makeText(ImageInsert.this, "User agreed to make required location settings changes.", Toast.LENGTH_LONG).show();
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(ImageInsert.this, "User chose not to make required location settings changes.", Toast.LENGTH_LONG).show();
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
        }
    }

    private void stopLocationUpdates() {
//        if (!mRequestingLocationUpdates) {
//            Toast.makeText(getApplicationContext(),"stopLocationUpdates: updates never requested, no-op." ,Toast.LENGTH_LONG).show();
//            return;
//        }
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;
                    }
                });
    }

    private void getAddress (Location location) throws IOException, JSONException {
        String uriAPI = "https://maps.googleapis.com/maps/api/geocode/json?latlng=%s&key=%s";
        //构造Geocoder API的完整URL
        String coordinate = location.getLatitude() + "," + location.getLongitude();
        String key = "AIzaSyA0Au4sLmmtDyl1Ge5sfpiEUf_CHKRrL6M";
        String url = String.format(uriAPI, coordinate, key);
System.out.println("COORDINATES: " + coordinate);


        boolean jonascrib = distFrom(48.483693, 9.186810, location.getLatitude(), location.getLongitude(), 30);
        boolean buildingnine = distFrom(48.482922, 9.18792, location.getLatitude(), location.getLongitude(), 50);
        boolean mensa = distFrom(48.482217,9.188579, location.getLatitude(), location.getLongitude(), 43);
        boolean library = distFrom(48.481915,9.186916, location.getLatitude(), location.getLongitude(), 34);
        boolean buildingtwenty = distFrom(48.481033,9.186095, location.getLatitude(), location.getLongitude(), 50);




        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            int code = conn.getResponseCode();
            if (code == 200) {

                InputStream is = conn.getInputStream();
                String resultStr = GetJson.readStream(is);

                //解析根元素，得到一个数组
                JSONArray jsonObjs = new JSONObject(resultStr).getJSONArray("results");

                //取出数组中第一个json对象(本示例数组中实际只包含一个元素)
                JSONObject jsonObj = jsonObjs.getJSONObject(0);

                //解析得formatted_address值
                String address = jsonObj.getString("formatted_address");

//                Toast.makeText(getApplicationContext(), "Location: " + address, Toast.LENGTH_LONG).show();
                Address = address;

                if (jonascrib){
                    Toast.makeText(getApplicationContext(), "Location: jonas lolz place", Toast.LENGTH_LONG).show();
                    currentlocation = "Jonas house";
                }else if(buildingnine) {
                    Toast.makeText(getApplicationContext(), "Location: building 9", Toast.LENGTH_LONG).show();
                    currentlocation = "Building 9";
                }else if(mensa) {
                    Toast.makeText(getApplicationContext(), "Location: Mensa", Toast.LENGTH_LONG).show();
                    currentlocation = "Mensa";
                }else if(library) {
                    Toast.makeText(getApplicationContext(), "Location: Library", Toast.LENGTH_LONG).show();
                    currentlocation = "The library";
                }else if(buildingtwenty) {
                    Toast.makeText(getApplicationContext(), "Location: Building 20", Toast.LENGTH_LONG).show();
                    currentlocation = "Building 20";
                }else {
                    Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();
                    currentlocation = address;
                }

                System.out.println("CUR LOCATION get address: " + currentlocation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean distFrom(double lat1, double lng1, double lat2, double lng2, double radius) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = (double) (earthRadius * c);

        if(dist < radius){
            return true;
        }

        return false;
    }


}
