package com.derric.quickbar;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import com.derric.quickbar.models.AppInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Tell the QuickBarManager class what to do
 * Manages the quickbar floating view
 * mContext  means managerContext , m --> manager
 */
public class QuickBarManager {

    private final Context mContext;
    private final WindowManager mWindowManager;
    //Stores each Quickbars added to the screen
    private final List<View> mQuickBars;

    public QuickBarManager(Context context) {
        this.mContext = context;
        this.mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.mQuickBars = new ArrayList<>();
    }

    public void addToWindow(View linearLayout) {
        QuickBar quickBar = new QuickBar(mContext);
        mQuickBars.add(linearLayout);
        ImageView hideBarArrow = linearLayout.findViewById(R.id.hide_arrow);
        //Hide quickbar when back arrow is pressed
        hideBarArrow.setOnClickListener(v -> {
            linearLayout.setVisibility(View.GONE);
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ImageView showIcon = (ImageView) inflater.inflate(R.layout.icon_layout, null, false);
            showIcon.setOnClickListener(vi -> {
                showIcon.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
            });
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.gravity = Gravity.RIGHT | Gravity.CENTER_HORIZONTAL;
            params.width = 60;
            params.height = 60;
            params.type = Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1 ?
                    WindowManager.LayoutParams.TYPE_PRIORITY_PHONE : WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    //Allow window to extend outside of the screen
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
            mWindowManager.addView(showIcon, params);
        });
        LinearLayout layout2 = linearLayout.findViewById(R.id.third_linear);
        getAllApps(layout2);
        mWindowManager.addView(linearLayout, quickBar.windowLayoutParams);
    }

    private void getAllApps(LinearLayout innerLayout) {
        LinearLayout.LayoutParams iconSize = new LinearLayout.LayoutParams(110, 110);
        PackageManager packageManager = mContext.getPackageManager();
        List<AppInfo> appInfos = getAllInstalledApps(packageManager);
        for (AppInfo appInfo : appInfos) {
            Intent mainActivityIntent = packageManager.getLaunchIntentForPackage(appInfo.getPackageName());
            //Exclude apps which don't have main activity (Avoid system services apps which don't have an UI)
            if (mainActivityIntent != null) {
                ImageView iconView = new ImageView(mContext);
                iconView.setImageDrawable(appInfo.getIcon());
                //Set icon's height and width
                iconView.setLayoutParams(iconSize);
                iconView.setOnClickListener((v) -> mContext.startActivity(mainActivityIntent));
                innerLayout.addView(iconView);
            }

        }
    }


    public void removeAllQuickBarsFromWindow() {
        for (View quickBar : mQuickBars) {
            mWindowManager.removeViewImmediate(quickBar);
        }
        mQuickBars.clear();
    }


    public List<AppInfo> getAllInstalledApps(PackageManager packageManager) {
        List<AppInfo> appInfos = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            List<ApplicationInfo> appList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            for (ApplicationInfo resolveInfo : appList) {
                AppInfo appInfo = new AppInfo();
                appInfo.setIcon(resolveInfo.loadIcon(packageManager));
                appInfo.setPackageName(resolveInfo.packageName);
                appInfos.add(appInfo);
            }
        } else {
            List<PackageInfo> packs = packageManager.getInstalledPackages(0);
            for (PackageInfo packageInfo : packs) {
                AppInfo appInfo = new AppInfo();
                appInfo.setIcon(packageInfo.applicationInfo.loadIcon(packageManager));
                appInfo.setPackageName(packageInfo.packageName);
                appInfos.add(appInfo);
            }
        }
        return appInfos;
    }
}
