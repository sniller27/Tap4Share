package com.vogella.android.tap4share;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {

//    private TextView topBar;
    private TextView tabHome;
    private TextView tabTap;

//    private FrameLayout ly_content;

    private FirstFragment f1,f2;
//    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        bindView();

    }

    //UI initialize
    private void bindView() {
//        topBar = (TextView)this.findViewById(R.id.text_top);
        tabHome = (TextView)this.findViewById(R.id.text_home);
        tabTap = (TextView)this.findViewById(R.id.text_tap);
//        ly_content = (FrameLayout) findViewById(R.id.fragment_container);

        tabHome.setOnClickListener(this);
        tabTap.setOnClickListener(this);

    }

    //reset selected text
    public void selected(){
        tabHome.setSelected(false);
        tabTap.setSelected(false);
    }

    //hide all fragments
    public void hideAllFragment(FragmentTransaction transaction){
        if(f1!=null){
            transaction.hide(f1);
        }
        if(f2!=null){
            transaction.hide(f2);
        }
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        switch(v.getId()){
            case R.id.text_home:
                selected();
                tabHome.setSelected(true);
                if(f1==null){
                    f1 = new FirstFragment("First Fragment");
                    transaction.add(R.id.fragment_container,f1);
                }else{
                    transaction.show(f1);
                }
                break;

            case R.id.text_tap:
                selected();
                tabTap.setSelected(true);
                if(f2==null){
                    f2 = new FirstFragment("Second Fragment");
                    transaction.add(R.id.fragment_container,f2);
                }else{
                    transaction.show(f2);
                }
                break;

        }

        transaction.commit();
    }
}
