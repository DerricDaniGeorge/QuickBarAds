package com.derric.quickbarads;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.derric.quickbarads.constants.AppConstants;
import com.derric.quickbarads.fragments.OrderAppsFragment;
import com.derric.quickbarads.models.AppInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class OrderAppsActivity extends AppCompatActivity {

    private ArrayList<AppInfo> userSelectedApps;
    private OrderAppsFragment orderAppsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userSelectedApps = (ArrayList<AppInfo>) getIntent().getSerializableExtra(AppConstants.APP_INFOS);
        //To set a fragment to an activity, we have to first create a fragment transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //Create the fragment object
        orderAppsFragment = new OrderAppsFragment(userSelectedApps);
        //Add the main menu fragment to the activity layout
        transaction.replace(R.id.container, orderAppsFragment);
        transaction.commit();
    }

    //When the back button / back event occurred
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveAppsOrder();
//        System.out.println("Selected apps are: "+selectedApps);
        Toast.makeText(this,"Please stop and re-launch quickbar if there is any changes",Toast.LENGTH_SHORT).show();
    }

    private void saveAppsOrder() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> selectedApps = new HashSet<>();
        int count = 1;
        for (AppInfo appInfo : userSelectedApps) {
            appInfo.setPosition(count++);
            selectedApps.add(appInfo.getPackageName()+":"+count);
//            System.out.println("Saving app____________: "+appInfo.getAppName()+" count::::"+count);
        }
        editor.putStringSet("selectedApps", selectedApps);
        editor.commit();
    }

    @Override
    public void onDestroy(){
        saveAppsOrder();
        super.onDestroy();
    }

    @Override
    public void onPause(){
        saveAppsOrder();
        super.onPause();
    }
}