package com.derric.quickbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
    private final List<QuickBar> mQuickBars;

    public QuickBarManager(Context context) {
        this.mContext = context;
        this.mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.mQuickBars = new ArrayList<>();
    }

    public void addViewToWindow(View barView) {
        //Here now a FrameLayout is created as the QuickBar class extends FrameLayout
        final QuickBar quickBar = new QuickBar(mContext);
        //Set barView image size. Set it to the size of framelayout it self, so the bar panel covers the entire framelayout area.
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.MATCH_PARENT);
        barView.setLayoutParams(params);
        //Add the barView image to the framelayout(Quickbar)
        quickBar.addView(barView);
        PackageManager packageManager = mContext.getPackageManager();
        List<AppInfo> appInfos = getAllInstalledApps(packageManager);
        for(AppInfo appInfo: appInfos){
            Intent mainActivityIntent = packageManager.getLaunchIntentForPackage(appInfo.getPackageName());
            //Exclude apps which don't have main activity (Avoid system services apps which don't have an UI)
            if(mainActivityIntent != null){
                ImageView iconView = new ImageView(mContext);
                iconView.setImageDrawable(appInfo.getIcon());
                iconView.setOnClickListener((v) -> mContext.startActivity(mainActivityIntent));
                quickBar.addView(iconView);
            }

        }
        //Add the quickBar to a list, so that we can access the quickbar to delete it later when service stops.
        mQuickBars.add(quickBar);
        //Now Get the layout settings defined in Quickbar class and add the framelayout to the screen/display by telling the manager to do it.
        mWindowManager.addView(quickBar, quickBar.getWindowLayoutParams());
    }

    public void removeAllQuickBarsFromWindow() {
        for (QuickBar quickBar : mQuickBars) {
            mWindowManager.removeViewImmediate(quickBar);
        }
        mQuickBars.clear();
    }

    public List<AppInfo> getAllInstalledApps(PackageManager packageManager) {
//        //Get list of installed apps.
//        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
//        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//        List<ResolveInfo> appList = packageManager.queryIntentActivities(mainIntent, 0);
//        int id = -1;
//        Drawable icon=null;
//        String packageName = null;
//        for(ResolveInfo resolveInfo: appList){
////
//           if(resolveInfo.activityInfo.packageName.contains("gall")){
//               id = resolveInfo.activityInfo.icon;
//               packageName = resolveInfo.activityInfo.packageName;
//              icon= resolveInfo.activityInfo.loadIcon(packageManager);
//           }
//        Log.i("packageName",resolveInfo.activityInfo.packageName);
//        Log.i("icon",resolveInfo.activityInfo.loadIcon(packageManager).toString());
//        }
////        Log.d("icon",packageName);
////        Log.d("id", String.valueOf(id));
//Todo : Below code will only  work in phones below Aroid Oreo, need to use above code for above oreo phones
        List<AppInfo> appInfos = new ArrayList<>();
        List<PackageInfo> packs = packageManager.getInstalledPackages(0);
        for(int i=0;i<50;i++){
            PackageInfo packageInfo = packs.get(i);
            AppInfo appInfo = new AppInfo();
            appInfo.setIcon(packageInfo.applicationInfo.loadIcon(packageManager));
            appInfo.setPackageName(packageInfo.packageName);
            appInfos.add(appInfo);
        }
        return appInfos;
    }
}
