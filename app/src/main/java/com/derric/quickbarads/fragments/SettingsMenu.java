package com.derric.quickbarads.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.derric.quickbarads.ChooseAppsActivity;
import com.derric.quickbarads.OrderAppsActivity;
import com.derric.quickbarads.QuickBarUtils;
import com.derric.quickbarads.R;
import com.derric.quickbarads.constants.AppConstants;
import com.derric.quickbarads.models.AppInfo;

import java.util.ArrayList;

public class SettingsMenu extends PreferenceFragmentCompat {

    private ArrayList<AppInfo> appInfos;
    private boolean isSettingsChanged;

    public SettingsMenu(ArrayList<AppInfo> appInfos) {
        this.appInfos = appInfos;
    }

    public boolean isSettingsChanged() {
        return this.isSettingsChanged;
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

        Preference orderApps = findPreference("orderApps");
        orderApps.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent orderAppsIntent = new Intent(getActivity(), OrderAppsActivity.class);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                //Show only user selected Apps here
                ArrayList<AppInfo> userSelectedApps = QuickBarUtils.getUserSelectedApps(preferences.getStringSet("selectedApps", null), appInfos);
                orderAppsIntent.putExtra(AppConstants.APP_INFOS, userSelectedApps);
                startActivity(orderAppsIntent);
                return true;
            }
        });

//        SeekBarPreference barTransparencySeekBar = (SeekBarPreference) findPreference("quickbarTransparency");
//        barTransparencySeekBar

    }


}
