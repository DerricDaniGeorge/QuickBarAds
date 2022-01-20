package com.derric.quickbar;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.derric.quickbar.models.AppInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Tell the QuickBarManager class what to do
 * Manages the quickbar floating view
 * mContext  means managerContext , m --> manager
 */
public class QuickBarManager {

    private final Context mContext;
    private final WindowManager mWindowManager;
    //Stores each Views added to the screen
    private final List<View> mViews;
    //Stores showIcon
    private ImageView showIcon;

    public QuickBarManager(Context context) {
        this.mContext = context;
        this.mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.mViews = new ArrayList<>();
    }

    public static class Settings {
        public boolean useTransparentBackground;
        public boolean showAppsInAscendingOrder;
        public boolean autoStartAppOnBoot;
        public boolean hideQuickBarOnAppLaunch;
        public boolean hideQuickBarLogo;
        public boolean showAllApps;
        public String quickbarChooseSide;
    }

    public void addToWindow(View relativeLayout, QuickBarManager.Settings settings, ArrayList<AppInfo> appInfos) {
        QuickBar quickBar = new QuickBar(mContext, settings);

        mViews.add(relativeLayout);
        //New implementation starts -->
        ImageView hideArrow = relativeLayout.findViewById(R.id.hide_arrow);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if(settings.quickbarChooseSide.equals("Right")){
            showIcon = (ImageView) inflater.inflate(R.layout.icon_layout_left, null, false);
        }else{
            showIcon = (ImageView) inflater.inflate(R.layout.icon_layout_right, null, false);
        }
        //Hide the show icon first and then only add to screen
        showIcon.setVisibility(View.GONE);
        WindowManager.LayoutParams params = getLayoutParams(settings);
        //When show button is clicked, show the quickbar
        showIcon.setOnClickListener(vi -> {
            showIcon.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
        });
        //Add the icon to the screen
        mWindowManager.addView(showIcon, params);
        hideArrow.setOnClickListener(v -> {
            relativeLayout.setVisibility(View.GONE);
            showIcon.setVisibility(View.VISIBLE);
        });
        // Add the show arrow to the list, so that we can remove it from screen when the service is stopped
        mViews.add(showIcon);
        LinearLayout thirdLinear = relativeLayout.findViewById(R.id.third_linear);
        if (settings.useTransparentBackground) {
            relativeLayout.findViewById(R.id.second_linear).setBackgroundColor(Color.TRANSPARENT);
        }
        System.out.println("Appinfos size:"+appInfos.size());
        getAllApps2(thirdLinear, settings, relativeLayout,appInfos);
        mWindowManager.addView(relativeLayout, quickBar.windowLayoutParams);
    }

    @NonNull
    private WindowManager.LayoutParams getLayoutParams(QuickBarManager.Settings settings) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        if (settings.quickbarChooseSide.equals("Right")) {
            params.gravity = Gravity.RIGHT | Gravity.CENTER;
        } else {
            params.gravity = Gravity.LEFT | Gravity.CENTER;
        }
        params.width = 60;
        params.height = 60;
        params.type = Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1 ?
                WindowManager.LayoutParams.TYPE_PRIORITY_PHONE : WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                //Allow window to extend outside of the screen
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        return params;
    }

//    private void getAllApps(LinearLayout innerLayout, QuickBarManager.Settings settings, View relativeLayout) {
//        LinearLayout.LayoutParams iconSize = new LinearLayout.LayoutParams(110, 110);
//        PackageManager packageManager = mContext.getPackageManager();
//        List<AppInfo> appInfos = getAllInstalledApps(packageManager);
//        if (settings.showAppsInAscendingOrder) {
//            sortAppsByName(appInfos);
//        }
//        for (AppInfo appInfo : appInfos) {
//            Intent mainActivityIntent = packageManager.getLaunchIntentForPackage(appInfo.getPackageName());
//            //Exclude apps which don't have main activity (Avoid system services apps which don't have an UI)
//            if (mainActivityIntent != null) {
//                ImageView iconView = new ImageView(mContext);
//                iconView.setImageDrawable(appInfo.getIcon());
//                //Set icon's height and width
//                iconView.setLayoutParams(iconSize);
//                iconView.setOnClickListener((v) -> {
//                    mContext.startActivity(mainActivityIntent);
//                    if (settings.hideQuickBarOnAppLaunch) {
//                        relativeLayout.setVisibility(View.GONE);
//                        showIcon.setVisibility(View.VISIBLE);
//                    }
//                });
//                innerLayout.addView(iconView);
//            }
//        }
//    }

    private void getAllApps2(LinearLayout innerLayout, QuickBarManager.Settings settings, View relativeLayout,ArrayList<AppInfo> appInfos) {
        LinearLayout.LayoutParams iconSize = new LinearLayout.LayoutParams(110, 110);
        PackageManager packageManager = mContext.getPackageManager();
//        List<AppInfo> appInfos = getAllInstalledApps(packageManager);
        if (settings.showAppsInAscendingOrder) {
            sortAppsByName(appInfos);
        }
        for (AppInfo appInfo : appInfos) {
            System.out.println("appIcon: "+appInfo.getIcon());
            Intent mainActivityIntent = packageManager.getLaunchIntentForPackage(appInfo.getPackageName());
            //Exclude apps which don't have main activity (Avoid system services apps which don't have an UI)
            if (mainActivityIntent != null) {
                ImageView iconView = new ImageView(mContext);
//                byte[] imageBytes = appInfo.getIcon();
//                Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
//                iconView.setImageBitmap(bmp);
                File filePath = mContext.getFileStreamPath(appInfo.getIconPath());
                Drawable icon = Drawable.createFromPath(filePath.toString());
                iconView.setImageDrawable(icon);
                //Set icon's height and width
                iconView.setLayoutParams(iconSize);
                iconView.setOnClickListener((v) -> {
                    mContext.startActivity(mainActivityIntent);
                    if (settings.hideQuickBarOnAppLaunch) {
                        relativeLayout.setVisibility(View.GONE);
                        showIcon.setVisibility(View.VISIBLE);
                    }
                });
                innerLayout.addView(iconView);
            }
        }
    }



    public void removeAllQuickBarsFromWindow() {
        for (View quickBar : mViews) {
            mWindowManager.removeViewImmediate(quickBar);
        }
        mViews.clear();
    }


    public static List<AppInfo> getAllInstalledApps(PackageManager packageManager, Context context) {
        List<AppInfo> appInfos = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            List<ApplicationInfo> appList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            for (ApplicationInfo applicationInfo : appList) {
                AppInfo appInfo = new AppInfo();
                Drawable drawable = applicationInfo.loadIcon(packageManager);
                //Convert image to bytes
                byte[] bitmapData = getDrawableBytes((BitmapDrawable) drawable);
                String packageName = applicationInfo.packageName;
                appInfo.setPackageName(packageName);
                if(packageName.contains(".")){
                   appInfo.setIconPath(packageName.replaceAll(".","")+".png");
                }else{
                    appInfo.setIconPath(packageName+".png");
                }
                saveAppIcon(context, appInfo, bitmapData);
                appInfo.setAppName(packageManager.getApplicationLabel(applicationInfo).toString());
                appInfos.add(appInfo);
//                Log.d("appNames", appInfo.getAppName());
            }
            appInfos.addAll(getDialerApps(packageManager, context));

        } else {
            List<PackageInfo> packs = packageManager.getInstalledPackages(0);
            for (PackageInfo packageInfo : packs) {
                AppInfo appInfo = new AppInfo();
                Drawable drawable = packageInfo.applicationInfo.loadIcon(packageManager);
                byte[] bitmapData = getDrawableBytes((BitmapDrawable) drawable);
                String packageName = packageInfo.packageName;
                appInfo.setPackageName(packageName);
                if(packageName.contains(".")){
                    appInfo.setIconPath(packageName.replaceAll(".","")+".png");
                }else{
                    appInfo.setIconPath(packageName+".png");
                }
                saveAppIcon(context, appInfo, bitmapData);
                appInfos.add(appInfo);
                appInfo.setAppName(packageManager.getApplicationLabel(packageInfo.applicationInfo).toString());
//                Log.d("appNames", appInfo.getAppName());
            }
            appInfos.addAll(getDialerApps(packageManager,context));
        }
        return appInfos;
    }

    private static void saveAppIcon(Context context, AppInfo appInfo, byte[] bitmapData) {
        if(bitmapData != null){
            try{
                FileOutputStream fileOutputStream = context.openFileOutput(appInfo.getIconPath(), Context.MODE_PRIVATE);
                fileOutputStream.write(bitmapData);
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Convert Drawable icon to bytes array
     * @param drawable
     * @return
     */
    private static byte[] getDrawableBytes(BitmapDrawable drawable) {
        if(drawable != null){
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            return stream.toByteArray();
        }
        return null;
    }

    public static List<AppInfo> getDialerApps(PackageManager packageManager, Context context) {
        List<AppInfo> dialerApps = new ArrayList<>();
        final Intent dialerIntent = new Intent();
        dialerIntent.setAction(Intent.ACTION_DIAL);
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(dialerIntent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            AppInfo appInfo = new AppInfo();
            Drawable drawable = resolveInfo.loadIcon(packageManager);
            byte[] bitmapData = getDrawableBytes((BitmapDrawable) drawable);
            String packageName = resolveInfo.activityInfo.packageName;
            appInfo.setPackageName(packageName);
            if(packageName.contains(".")){
                appInfo.setIconPath(packageName.replaceAll(".","")+".png");
            }else{
                appInfo.setIconPath(packageName+".png");
            }
            saveAppIcon(context, appInfo, bitmapData);
            appInfo.setAppName(packageManager.getApplicationLabel(resolveInfo.activityInfo.applicationInfo).toString());
            dialerApps.add(appInfo);
        }
        return dialerApps;
    }

    public void sortAppsByName(List<AppInfo> appInfos) {
        class AppNameAscendingComparator implements Comparator<AppInfo> {
            @Override
            public int compare(AppInfo o1, AppInfo o2) {
                return o1.getAppName().compareTo(o2.getAppName());
            }
        }
        Collections.sort(appInfos, new AppNameAscendingComparator());
    }


}
