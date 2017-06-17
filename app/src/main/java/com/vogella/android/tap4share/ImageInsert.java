package com.vogella.android.tap4share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

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
    }
}
