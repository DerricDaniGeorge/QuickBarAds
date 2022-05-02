package com.derric.quickbarads.fragments;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.derric.quickbarads.R;
import com.derric.quickbarads.constants.AppConstants;
import com.derric.quickbarads.fragments.SettingsMenu;
import com.derric.quickbarads.models.AppInfo;
import com.derric.quickbarads.services.QuickBarService;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainMenu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainMenu extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 100;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<AppInfo> appInfos;
    private SettingsMenu settingsMenu;
    private static boolean testMode = true;
    BannerView topBanner;
    BannerView bottomBanner;

    public MainMenu() {
        // Required empty public constructor
    }

    public SettingsMenu getSettingsMenu() {
        return settingsMenu;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainMenu.
     */
    // TODO: Rename and change types and number of parameters
    public static MainMenu newInstance(String param1, String param2) {
        MainMenu fragment = new MainMenu();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize Unity ads
        UnityAds.initialize(getContext(),AppConstants.GAME_ID,testMode);
        topBanner = new BannerView(getActivity(),AppConstants.BANNER_ID_TOP, new UnityBannerSize(320,50));
        bottomBanner = new BannerView(getActivity(),AppConstants.BANNER_ID_BOTTOM, new UnityBannerSize(320,50));
        if (getArguments() != null) {
            appInfos = (ArrayList<AppInfo>)  getArguments().getSerializable(AppConstants.APP_INFOS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and attach to container
        View menuView = inflater.inflate(R.layout.fragment_main_menu, container, false);
        LinearLayout topBannerLayout = menuView.findViewById(R.id.topBannerAd);
        topBanner.load();
        topBannerLayout.addView(topBanner);

        LinearLayout bottomBannerLayout = menuView.findViewById(R.id.bottomBannerAd);
        bottomBanner.load();
        bottomBannerLayout.addView(bottomBanner);
        //On clicking "Launch QuickBar" button
        menuView.findViewById(R.id.launch_quickbar_button).setOnClickListener((v -> {
            showQuickBar(getActivity(), true);
        }));
        menuView.findViewById(R.id.settings_button).setOnClickListener(v -> {
            final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            settingsMenu = new SettingsMenu(appInfos);
            ft.replace(R.id.container, settingsMenu);
            ft.addToBackStack("settingsFragment");
            ft.commit();
        });
        //Stop QuickBar service
        menuView.findViewById(R.id.stop_service_button).setOnClickListener(v -> {
            Intent stopIntent = new Intent(getActivity(), QuickBarService.class);
            //Get the activity and call stop service
            getActivity().stopService(stopIntent);
        });
        return menuView;
    }

    private void showQuickBar(Context context, boolean askForOverlayPermission) {
        //If the android os running in this device is less than lollipop, then directly start the service,
        //no need to ask for overlay permission
        //API 22
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            startQuickBarService(getActivity());
            //Don't run the rest of code in this method
            return;
        }
        //This method call requires API 23 or above , so do a if check
        //Check if quick bar can be displayed on top of other apps
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(context)) {
                startQuickBarService(getActivity());
                //Don't run the rest of code in this method
                return;
            }
        }
        //if none of the above if conditions satisfies means, we have to ask the user for overlay permission
        if (askForOverlayPermission) {
            Intent askIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
            //We must add "SYSTEM_ALERT_WINDOW" in android manifest file...otherwise it won't work
            startActivityForResult(askIntent, OVERLAY_PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            showQuickBar(getActivity(), false);
        }
    }

    private void startQuickBarService(Activity activity) {
        Class<? extends Service> service = QuickBarService.class;
        Intent startIntent = new Intent(activity, service);
        //Add the apps info data, so that it can retrieved at other end
        startIntent.putExtra(AppConstants.APP_INFOS,appInfos);
        ContextCompat.startForegroundService(activity, startIntent);
    }

}