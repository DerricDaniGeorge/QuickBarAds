package com.derric.quickbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.derric.quickbar.constants.AppConstants;
import com.derric.quickbar.fragments.ChooseAppsFragment;
import com.derric.quickbar.fragments.MainMenu;
import com.derric.quickbar.models.AppInfo;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ChooseAppsActivity extends AppCompatActivity {

    private ArrayList<AppInfo> appInfos;
    private  Set<String> selectedApps;
    private ChooseAppsFragment chooseAppsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set main activity layout
        setContentView(R.layout.activity_main);
        appInfos = (ArrayList<AppInfo>) getIntent().getSerializableExtra(AppConstants.APP_INFOS);
        //To set a fragment to an activity, we have to first create a fragment transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //Create the fragment object
        chooseAppsFragment = new ChooseAppsFragment(appInfos);
        //Add the main menu fragment to the activity layout
        transaction.replace(R.id.container, chooseAppsFragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //Show select all icon in Action bar of the activity
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select_apps, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //When the back button / back event occurred
    @Override
    public void onBackPressed(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
//        StringBuilder builder = new StringBuilder();
        selectedApps = new HashSet<>();
        for(AppInfo appInfo : appInfos){
            if(appInfo.isSelected()){
//                builder.append(appInfo.getPackageName()).append(',');
//                System.out.println("App: "+appInfo.getPackageName()+" saved to settings");
                selectedApps.add(appInfo.getPackageName());
            }
        }
        if(chooseAppsFragment.getAllAppsSelected()){
            editor.putBoolean("wasAllAppsSelected",true);
        }else{
            editor.putBoolean("wasAllAppsSelected",false);
        }
        editor.putStringSet("selectedApps",selectedApps);
        editor.commit();
        super.onBackPressed();
    }


}