package com.derric.quickbar;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.derric.quickbar.models.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Tell the QuickBarManager class what to do
 * Manages the quickbar floating view
 * mContext  means managerContext , m --> manager
 */
public class QuickBarManager {

    private final Context mContext;
    private final WindowManager mWindowManager;
    //Stores each Quickbars added to the screen
    private final List<ScrollView> mQuickBars;

    private final List<View> mQuickBars2;

    public QuickBarManager(Context context) {
        this.mContext = context;
        this.mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.mQuickBars = new ArrayList<>();
        this.mQuickBars2=new ArrayList<>();
    }

    public void addToWindow2(View linearLayout){
        QuickBar2 quickBar2 = new QuickBar2(mContext);
        mQuickBars2.add(linearLayout);
        LinearLayout layout2 = linearLayout.findViewById(R.id.innerlinear);
        getAllApps2(layout2);
        mWindowManager.addView(linearLayout,quickBar2.windowLayoutParams);
    }
    public void addViewToWindow(View barView, ScrollView scrollView) {
        //Here now a FrameLayout is created as the QuickBar class extends FrameLayout
        final QuickBar quickBar = new QuickBar(mContext);
        //Set barView image size. Set it to the size of framelayout it self, so the bar panel covers the entire framelayout area.
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.MATCH_PARENT);
        barView.setLayoutParams(params);
        //Add the barView image to the framelayout(Quickbar)
        quickBar.addView(barView);
        scrollView.addView(quickBar);
        setAppIcons(quickBar);
        //Add the quickBar to a list, so that we can access the quickbar to delete it later when service stops.
        mQuickBars.add(scrollView);
        //Now Get the layout settings defined in Quickbar class and add the framelayout to the screen/display by telling the manager to do it.
        mWindowManager.addView(scrollView, quickBar.getWindowLayoutParams());
    }

    private void setAppIcons(QuickBar quickBar) {
        PackageManager packageManager = mContext.getPackageManager();
        List<AppInfo> appInfos = getAllInstalledApps(packageManager);
        for (AppInfo appInfo : appInfos) {
            Intent mainActivityIntent = packageManager.getLaunchIntentForPackage(appInfo.getPackageName());
            //Exclude apps which don't have main activity (Avoid system services apps which don't have an UI)
            if (mainActivityIntent != null) {
                ImageView iconView = new ImageView(mContext);
                iconView.setImageDrawable(appInfo.getIcon());
                iconView.setOnClickListener((v) -> mContext.startActivity(mainActivityIntent));
                quickBar.addView(iconView);
            }

        }
    }

    private void getAllApps2(LinearLayout innerLayout ) {
        LinearLayout.LayoutParams iconSize = new LinearLayout.LayoutParams(110,110);
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
        for (ScrollView quickBar : mQuickBars) {
            mWindowManager.removeViewImmediate(quickBar);
        }
        mQuickBars.clear();
    }

    public void removeAllQuickBarsFromWindow2() {
        for (View quickBar : mQuickBars2) {
            mWindowManager.removeViewImmediate(quickBar);
        }
        mQuickBars2.clear();
    }


    public List<AppInfo> getAllInstalledApps(PackageManager packageManager) {
        List<AppInfo> appInfos = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            List<ApplicationInfo> appList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            //Get list of installed apps.
//            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
//            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//            List<ResolveInfo> appList = packageManager.queryIntentActivities(mainIntent, 0);
//            for (ResolveInfo resolveInfo : appList) {
//                AppInfo appInfo = new AppInfo();
//                appInfo.setIcon(resolveInfo.activityInfo.loadIcon(packageManager));
//                appInfo.setPackageName(resolveInfo.activityInfo.packageName);
//                appInfos.add(appInfo);
//            }
            for (ApplicationInfo resolveInfo : appList) {
                AppInfo appInfo = new AppInfo();
                appInfo.setIcon(resolveInfo.loadIcon(packageManager));
                appInfo.setPackageName(resolveInfo.packageName);
                appInfos.add(appInfo);
            }
        } else {
            List<PackageInfo> packs = packageManager.getInstalledPackages(0);
            for (PackageInfo packageInfo: packs) {
                AppInfo appInfo = new AppInfo();
                appInfo.setIcon(packageInfo.applicationInfo.loadIcon(packageManager));
                appInfo.setPackageName(packageInfo.packageName);
                appInfos.add(appInfo);
            }
        }

        return appInfos;
    }
}
