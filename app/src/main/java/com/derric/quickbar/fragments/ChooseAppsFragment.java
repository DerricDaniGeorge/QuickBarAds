package com.derric.quickbar.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.derric.quickbar.QuickBarManager;
import com.derric.quickbar.R;
import com.derric.quickbar.adapters.AppDataAdapter;
import com.derric.quickbar.adapters.ChooseAppAdapter;
import com.derric.quickbar.models.AppInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChooseAppsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChooseAppsFragment extends Fragment {
    //    private static final String APP_INFOS = "app_infos";
    private ArrayList<AppInfo> appInfos;
    private AppDataAdapter adapter;
    private boolean isAllAppsSelected;

    public ChooseAppsFragment(ArrayList<AppInfo> appInfos) {
        // Required empty public constructor
        this.appInfos = appInfos;
    }

    public boolean getAllAppsSelected() {
        return this.isAllAppsSelected;
    }

    public AppDataAdapter getAdapter() {
        return this.adapter;
    }


    // TODO: Rename and change types and number of parameters
//    public static ChooseAppsFragment newInstance(List<AppInfo> appInfos) {
//        ChooseAppsFragment fragment = new ChooseAppsFragment();
//        Bundle bundle = new Bundle();
//        ArrayList<AppInfo> apps = new ArrayList<>();
//        apps.addAll(appInfos);
//        bundle.putSerializable(APP_INFOS,apps);
//        fragment.setArguments(bundle);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Detect actionbar of the activity where this fragment is present
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_choose_apps, container, false);
        RecyclerView recyclerView = layout.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        PackageManager packageManager = getContext().getPackageManager();
//        List<AppInfo> appInfos = QuickBarManager.getAllInstalledApps(packageManager,getContext());
        List<AppInfo> appsWithActivity = new ArrayList<>();
        for (AppInfo appInfo : appInfos) {
            Intent mainActivityIntent = packageManager.getLaunchIntentForPackage(appInfo.getPackageName());
            if (mainActivityIntent != null) {
                appsWithActivity.add(appInfo);
            }
        }
        //If user selected apps are there, then when showing the list, mark those selected apps checked
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Set<String> selectedApps = preferences.getStringSet("selectedApps", null);
        if (selectedApps != null) {
            for (AppInfo appInfo : appsWithActivity) {
                if (selectedApps.contains(appInfo.getPackageName())) {
                    appInfo.setSelected(true);
                }
            }
        }
        adapter = new AppDataAdapter(appsWithActivity, getContext());
        recyclerView.setAdapter(adapter);
//        PackageManager packageManager = getContext().getPackageManager();
//        GridLayout grid = layout.findViewById(R.id.app_chooser_gridlayout);
//        List<AppInfo> appInfos = QuickBarManager.getAllInstalledApps(packageManager);
//        List<AppInfo> appsWithActivity = new ArrayList<>();
//        for(AppInfo appInfo:appInfos){
//            Intent mainActivityIntent = packageManager.getLaunchIntentForPackage(appInfo.getPackageName());
//            if(mainActivityIntent != null){
//                appsWithActivity.add(appInfo);
//            }
//        }
//        for(AppInfo appInfo: appsWithActivity){
//            LinearLayout eachItem = (LinearLayout) inflater.inflate(R.layout.choose_apps_item,grid,false);
//            ImageView appIcon = eachItem.findViewById(R.id.app_icon);
//            appIcon.setImageDrawable(appInfo.getIcon());
//            TextView appName = eachItem.findViewById(R.id.app_name);
//            appName.setText(appInfo.getAppName());
//            grid.addView(eachItem);
//        }
//        PackageManager packageManager = getContext().getPackageManager();
//        List<AppInfo> appInfos = QuickBarManager.getAllInstalledApps(packageManager);
//        List<AppInfo> appsWithActivity = new ArrayList<>();
//        for (AppInfo appInfo : appInfos) {
//            Intent mainActivityIntent = packageManager.getLaunchIntentForPackage(appInfo.getPackageName());
//            if (mainActivityIntent != null) {
//                appsWithActivity.add(appInfo);
//            }
//        }
//        ChooseAppAdapter adapter = new ChooseAppAdapter(appsWithActivity);
//        GridView gridView = layout.findViewById(R.id.app_chooser_gridview);
//        gridView.setAdapter(adapter);

        return layout;
    }

    //Perform action if any icon is clicked from the Action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.selectAll:
                //Select all checkboxes in the recyclerview
                this.adapter.selectAllItems();
                this.isAllAppsSelected = true;
                return true;

            case R.id.deselectAll:
                this.adapter.deselectAllItems();
                this.isAllAppsSelected = false;
                return true;
        }
        return false;
    }

}