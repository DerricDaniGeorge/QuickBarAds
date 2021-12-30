package com.derric.quickbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.derric.quickbar.fragments.ChooseAppsFragment;
import com.derric.quickbar.fragments.MainMenu;

public class ChooseAppsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set main activity layout
        setContentView(R.layout.activity_main);
        //To set a fragment to an activity, we have to first create a fragment transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //Create the fragment object
        ChooseAppsFragment chooseAppsFragment = new ChooseAppsFragment();
        //Add the main menu fragment to the activity layout
        transaction.add(R.id.container, chooseAppsFragment);
        transaction.commit();
    }
}