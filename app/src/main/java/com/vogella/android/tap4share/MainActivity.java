package com.vogella.android.tap4share;


import android.app.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends Activity{


    private TextView tabHome;
    private TextView tabTap;
    private ImageView Photo;

    static final int REQUEST_IMAGE_CAPTURE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        bindView();



        tabTap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabTap.setSelected(true);
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });
    }


    //UI initialize
    private void bindView() {
        tabHome = (TextView) this.findViewById(R.id.text_home);
        tabTap = (TextView) this.findViewById(R.id.text_tap);
        Photo=(ImageView)this.findViewById(R.id.photo);
//        tabHome.setOnClickListener(this);
//        tabTap.setOnClickListener(this);
    }

    //reset selected text
    public void selected() {
        tabHome.setSelected(false);
        tabTap.setSelected(false);
    }


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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap photoBitmap = (Bitmap) extras.get("data");
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Photo.setImageBitmap(photoBitmap);
        }
    }
}
