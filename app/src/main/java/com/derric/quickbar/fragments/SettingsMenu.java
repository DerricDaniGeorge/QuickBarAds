package com.derric.quickbar.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.derric.quickbar.ChooseAppsActivity;
import com.derric.quickbar.R;
import com.derric.quickbar.constants.AppConstants;
import com.derric.quickbar.models.AppInfo;

import java.util.ArrayList;

public class SettingsMenu extends PreferenceFragmentCompat {

    private ArrayList<AppInfo> appInfos;

    public SettingsMenu(ArrayList<AppInfo> appInfos) {
        this.appInfos = appInfos;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_layout, rootKey);
//        getPreferenceScreen().getExtras().putSerializable(AppConstants.APP_INFOS, appInfos);
        Preference chooseApp = findPreference("chooseApps");
        chooseApp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent chooseAppsIntent = new Intent(getActivity(), ChooseAppsActivity.class);
                chooseAppsIntent.putExtra(AppConstants.APP_INFOS, appInfos);
                startActivity(chooseAppsIntent);
                return true;
            }
        });
        SharedPreferences.OnSharedPreferenceChangeListener settingsChanged = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                //If any settings got changed, then do below:
            }
        };
    }



}
