package com.vogella.android.tap4share;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by lenovo on 2017/6/12.
 */

public class FirstFragment extends Fragment {
    private String context;
    private TextView mTextView;

    public FirstFragment(String context){
        this.context = context;
    }

//    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_fragment,container,false);
        mTextView = (TextView)view.findViewById(R.id.text_content);
        //mTextView = (TextView)getActivity().findViewById(R.id.txt_content);
        mTextView.setText(context);
        return view;
    }
}
