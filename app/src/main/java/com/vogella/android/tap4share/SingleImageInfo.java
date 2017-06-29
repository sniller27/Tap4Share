package com.vogella.android.tap4share;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class SingleImageInfo extends AppCompatActivity {

    TextView timestamp;
    TextView title;
    TextView description;
    TextView location;
    ImageView image;
    ServerConfig servconfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image_info);

        //constructor
        servconfig = new ServerConfig();

        timestamp = (TextView) findViewById(R.id.singleimage_timestamp);
        title = (TextView) findViewById(R.id.singleimage_title);
        description = (TextView) findViewById(R.id.singleimage_description);
        location = (TextView) findViewById(R.id.singleimage_location);
        image = (ImageView) findViewById(R.id.singleimage_image);

        Bundle extras = getIntent().getExtras();

        timestamp.setText(extras.getString("timestamp"));
        title.setText(extras.getString("title"));
        description.setText(extras.getString("description"));
        location.setText("Location: " + extras.getString("location"));

        //image
        Picasso.with(SingleImageInfo.this).load(servconfig.getSingle_image_by_name() + extras.getString("imagefilename")).into(image);
    }
}
