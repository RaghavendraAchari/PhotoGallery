package com.raghav.photogallery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Vighnesh on 22-04-2018.
 */

public abstract class SingleFragmentActivity extends AppCompatActivity{
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        //call fragmentMannager to put fragment
        FragmentManager fm = getSupportFragmentManager();
        //add fragment
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if(fragment==null){
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragment_container,fragment).commit();
        }

    }
}
