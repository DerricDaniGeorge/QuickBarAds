package com.derric.quickbarads;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.derric.quickbarads.constants.AppConstants;
import com.derric.quickbarads.fragments.OrderAppsFragment;
import com.derric.quickbarads.models.AppInfo;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class OrderAppsActivity extends AppCompatActivity implements IUnityAdsInitializationListener {

    private ArrayList<AppInfo> userSelectedApps;
    private OrderAppsFragment orderAppsFragment;
    private boolean isAdReady = false;

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
        Toast.makeText(this, "Please stop and re-launch quickbar if there is any changes", Toast.LENGTH_SHORT).show();
        UnityAds.show(this,AppConstants.INTER_AD_ORDER_APPS,new UnityAdsShowOptions(),showListener);
    }

    private void saveAppsOrder() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> selectedApps = new HashSet<>();
        int count = 1;
        for (AppInfo appInfo : userSelectedApps) {
            appInfo.setPosition(count++);
            selectedApps.add(appInfo.getPackageName() + ":" + count);
//            System.out.println("Saving app____________: "+appInfo.getAppName()+" count::::"+count);
        }
        editor.putStringSet("selectedApps", selectedApps);
        editor.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveAppsOrder();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveAppsOrder();
    }

    @Override
    public void onInitializationComplete() {
        UnityAds.load(AppConstants.INTER_AD_ORDER_APPS, loadListener);
    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError unityAdsInitializationError, String s) {

    }


    IUnityAdsLoadListener loadListener = new IUnityAdsLoadListener() {
        @Override
        public void onUnityAdsAdLoaded(String s) {
            isAdReady = true;
//            Log.i("Unityads", "Ad loadded");
        }

        @Override
        public void onUnityAdsFailedToLoad(String s, UnityAds.UnityAdsLoadError unityAdsLoadError, String s1) {

        }
    };
    IUnityAdsShowListener showListener = new IUnityAdsShowListener() {
        @Override
        public void onUnityAdsShowFailure(String s, UnityAds.UnityAdsShowError unityAdsShowError, String s1) {

        }

        @Override
        public void onUnityAdsShowStart(String s) {
            isAdReady = false;
        }

        @Override
        public void onUnityAdsShowClick(String s) {

        }

        @Override
        public void onUnityAdsShowComplete(String s, UnityAds.UnityAdsShowCompletionState unityAdsShowCompletionState) {
            UnityAds.load(AppConstants.INTER_AD_ORDER_APPS, loadListener);
        }
    };
}