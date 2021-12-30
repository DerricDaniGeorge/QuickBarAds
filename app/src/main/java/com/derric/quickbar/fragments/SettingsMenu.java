package com.derric.quickbar.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.derric.quickbar.R;

public class SettingsMenu extends PreferenceFragmentCompat{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_layout,rootKey);

    }
}
