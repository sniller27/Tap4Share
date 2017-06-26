package com.vogella.android.tap4share;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by jonas on 6/19/17.
 */

public class CustomListAdapter extends ArrayAdapter<ImageData> {

    private final Activity context;
    private ArrayList<ImageData> imgid;
    ServerConfig servconfig;
    public CustomListAdapter(Activity context, int itemname, ArrayList<ImageData> imgid) {
        super(context, itemname, imgid);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.imgid=imgid;
        servconfig = new ServerConfig();
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_item, null,true);


        TextView txtTitle = (TextView) rowView.findViewById(R.id.mobile);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.image);

        txtTitle.setText(imgid.get(position).getDescription());

            Picasso.with(getContext()).load(servconfig.getSingle_image_by_name() + imgid.get(position).getSource()).into(imageView);

        return rowView;

    };
}
