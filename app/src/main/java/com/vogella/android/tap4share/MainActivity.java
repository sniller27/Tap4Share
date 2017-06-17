package com.vogella.android.tap4share;


import android.app.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;


public class MainActivity extends AppCompatActivity {



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

            //Convert to byte array
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            picture.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byte[] byteArray = stream.toByteArray();
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            picture.compress(Bitmap.CompressFormat.PNG, 50, bs);

//            System.out.println("qreeeerefsdfsdfsd");
            Intent i = new Intent(this, ImageInsert.class);
            i.putExtra("camera_image", bs.toByteArray());
            startActivity(i);
        }
    }

}
