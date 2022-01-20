package com.derric.quickbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.derric.quickbar.constants.AppConstants;
import com.derric.quickbar.fragments.ChooseAppsFragment;
import com.derric.quickbar.fragments.MainMenu;
import com.derric.quickbar.models.AppInfo;

import java.util.ArrayList;

public class ChooseAppsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set main activity layout
        setContentView(R.layout.activity_main);
        ArrayList<AppInfo> appInfos = (ArrayList<AppInfo>) getIntent().getSerializableExtra(AppConstants.APP_INFOS);
        //To set a fragment to an activity, we have to first create a fragment transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //Create the fragment object
        ChooseAppsFragment chooseAppsFragment = new ChooseAppsFragment(appInfos);
        //Add the main menu fragment to the activity layout
        transaction.replace(R.id.container, chooseAppsFragment);
        transaction.commit();
    }
}