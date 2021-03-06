package com.vogella.android.tap4share;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class SingleImageInfo extends AppCompatActivity {

    private TextView timestamp;
    private TextView title;
    private TextView description;
    private TextView location;
    private ImageView image;
    private ServerConfig servconfig;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image_info);

        //assign server configurations
        servconfig = new ServerConfig();

        //assign UI-elements
        timestamp = (TextView) findViewById(R.id.singleimage_timestamp);
        title = (TextView) findViewById(R.id.singleimage_title);
        description = (TextView) findViewById(R.id.singleimage_description);
        location = (TextView) findViewById(R.id.singleimage_location);
        image = (ImageView) findViewById(R.id.singleimage_image);

        //get data passed from intent
        Bundle extras = getIntent().getExtras();

        //set UI-elements
        timestamp.setText(extras.getString("timestamp"));
        title.setText(extras.getString("title"));
        description.setText(extras.getString("description"));
        location.setText("Location: " + extras.getString("location"));

        //image
        Picasso.with(SingleImageInfo.this).load(servconfig.getSingle_image_by_name() + extras.getString("imagefilename")).into(image);
    }
}
