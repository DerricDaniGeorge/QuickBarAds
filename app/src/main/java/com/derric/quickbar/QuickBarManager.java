package com.derric.quickbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.derric.quickbar.constants.AppConstants;
import com.derric.quickbar.models.AppInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        public String quickbarChooseSide;
        public String quickbarChoosePosition;
        public Set<String> selectedApps;
        public boolean wasAllAppsSelected;
        public int quickbarTransparency;
        public int hideIconTransparency;
        public int showIconTransparency;
        public int appIconSize;
        public int showIconSize;
        public int hideIconSize;
        public int hideQuickBarSeconds;
        public String showIconChooseSide;
        public String showIconChoosePosition;

    }

    public void addToWindow(View relativeLayout, QuickBarManager.Settings settings, ArrayList<AppInfo> appInfos) {
        QuickBar quickBar = new QuickBar(mContext, settings);
        LinearLayout secondLinear = relativeLayout.findViewById(R.id.second_linear);
        mViews.add(relativeLayout);
        //New implementation starts -->
        ImageView hideArrow = relativeLayout.findViewById(R.id.hide_arrow);
        //Set transparency for hide button
        hideArrow.setAlpha(settings.hideIconTransparency / 100F);
        int hideIconPixel = QuickBarUtils.dpToPx(settings.hideIconSize, mContext);
        LinearLayout.LayoutParams hideIconSize = new LinearLayout.LayoutParams(hideIconPixel, hideIconPixel);
        hideArrow.setLayoutParams(hideIconSize);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (settings.showIconChooseSide.equals(AppConstants.RIGHT)) {
            showIcon = (ImageView) inflater.inflate(R.layout.icon_layout_left, null, false);
        } else {
            showIcon = (ImageView) inflater.inflate(R.layout.icon_layout_right, null, false);
        }
        showIcon.setAlpha(settings.showIconTransparency / 100F);
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                relativeLayout.setVisibility(View.GONE);
                if (showIcon != null) {
                    showIcon.setVisibility(View.VISIBLE);
                }
            }
        };
        //Hide the show icon first and then only add to screen
        showIcon.setVisibility(View.GONE);
        //Get layout params for showIcon
        WindowManager.LayoutParams showIconParams = getLayoutParams(settings);
        //When show button is clicked, show the quickbar
        showIcon.setOnClickListener(vi -> {
            showIcon.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, settings.hideQuickBarSeconds * 1000);
        });
        //Add the icon to the screen
        mWindowManager.addView(showIcon, showIconParams);
        hideArrow.setOnClickListener(v -> {
            relativeLayout.setVisibility(View.GONE);
            showIcon.setVisibility(View.VISIBLE);
        });
        // Add the show arrow to the list, so that we can remove it from screen when the service is stopped
        mViews.add(showIcon);
        LinearLayout thirdLinear = relativeLayout.findViewById(R.id.third_linear);
        if (settings.useTransparentBackground) {
            secondLinear.setBackgroundColor(Color.TRANSPARENT);
        }
        secondLinear.setAlpha(settings.quickbarTransparency / 100F);
        System.out.println("Appinfos size:" + appInfos.size());
        getAllApps2(thirdLinear, settings, relativeLayout, appInfos);
        mWindowManager.addView(relativeLayout, quickBar.windowLayoutParams);
        //Hide quickbar after a delay
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, settings.hideQuickBarSeconds * 1000);
    }

    //Parameters for show icon
    @NonNull
    private WindowManager.LayoutParams getLayoutParams(QuickBarManager.Settings settings) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        //Tells which side of the screen we need to show the show icon
        QuickBarUtils.setGravityShowIcon(params,settings);
        int showIconPixels = QuickBarUtils.dpToPx(settings.showIconSize,mContext);
        params.width = showIconPixels;
        params.height = showIconPixels;
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

    private void getAllApps2(LinearLayout innerLayout, QuickBarManager.Settings settings, View relativeLayout, ArrayList<AppInfo> appInfos) {
//        LinearLayout.LayoutParams iconSize = new LinearLayout.LayoutParams(110, 110);
        int pixel = QuickBarUtils.dpToPx(settings.appIconSize, mContext);
        LinearLayout.LayoutParams iconSize = new LinearLayout.LayoutParams(pixel, pixel);
        PackageManager packageManager = mContext.getPackageManager();
//        List<AppInfo> appInfos = getAllInstalledApps(packageManager);

        if (settings.wasAllAppsSelected) {
            System.out.println(" wasAllAppsseleted is true");
            //Show all apps
            if (settings.showAppsInAscendingOrder) {
                sortAppsByName(appInfos);
            }
            showApps(innerLayout, settings, relativeLayout, appInfos, iconSize, packageManager);
        } else {
            //Show only user selected apps
            Set<String> userSelectedApps = settings.selectedApps;
            // This null check is required, if this app is a fresh install and user didn't choose
            //any apps yet
            if (userSelectedApps != null) {
                ArrayList<AppInfo> selectedApps = new ArrayList<>();
                for (String packageName : userSelectedApps) {
                    for (AppInfo appInfo : appInfos) {
                        if (appInfo.getPackageName().equals(packageName)) {
                            selectedApps.add(appInfo);
                        }
                    }
                }
                if (settings.showAppsInAscendingOrder) {
                    sortAppsByName(selectedApps);
                }
                showApps(innerLayout, settings, relativeLayout, selectedApps, iconSize, packageManager);
            } else {
                //There are no user selected apps, means user is using the app for first time
                //show some random 5 apps
                ArrayList<AppInfo> appsToShow = new ArrayList<>();
                int count = 0;
                for (AppInfo app : appInfos) {
                    if (count <= 5) {
                        Intent mainActivityIntent = packageManager.getLaunchIntentForPackage(app.getPackageName());
                        if (mainActivityIntent != null) {
                            appsToShow.add(app);
                            count++;
                        }
                    }
                }
                //save these 5 apps
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = preferences.edit();
                Set<String> selectedApps = new HashSet<>();
                for (AppInfo appInfo : appsToShow) {
                    selectedApps.add(appInfo.getPackageName());
                }
                editor.putStringSet("selectedApps", selectedApps);
                editor.commit();
                if (settings.showAppsInAscendingOrder) {
                    sortAppsByName(appsToShow);
                }
                showApps(innerLayout, settings, relativeLayout, appsToShow, iconSize, packageManager);
            }

        }
    }

    private void showApps(LinearLayout innerLayout, Settings settings, View relativeLayout, ArrayList<AppInfo> appInfos, LinearLayout.LayoutParams iconSize, PackageManager packageManager) {
        for (AppInfo appInfo : appInfos) {
//            System.out.println("appIcon: "+appInfo.getIcon());
            Intent mainActivityIntent = packageManager.getLaunchIntentForPackage(appInfo.getPackageName());
            //Exclude apps which don't have main activity (Avoid system services apps which don't have an UI)
            if (mainActivityIntent != null) {
                ImageView iconView = new ImageView(mContext);
//                byte[] imageBytes = appInfo.getIcon();
//                Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
//                iconView.setImageBitmap(bmp);
                File filePath = mContext.getFileStreamPath(appInfo.getIconPath());
//                Log.d("filePathh",filePath.toString());
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
//            List<ApplicationInfo> appList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            List<ApplicationInfo> appList = packageManager.getInstalledApplications(0);

            for (ApplicationInfo applicationInfo : appList) {
                String packageName = applicationInfo.packageName;
                AppInfo appInfo = new AppInfo();
                Drawable drawable = applicationInfo.loadIcon(packageManager);
                //Convert image to bytes
                byte[] bitmapData = getDrawableBytes(getBitMapFromDrawable(drawable));
                System.out.println("Package name is: " + packageName);
                appInfo.setPackageName(packageName);
                if (packageName.contains(".")) {
                    appInfo.setIconPath(packageName.replaceAll("\\.", "") + ".png");
//                   System.out.println("getAllInstalledApps Inside if -->"+appInfo.getIconPath());
                } else {
                    appInfo.setIconPath(packageName + ".png");
//                    System.out.println("getAllInstalledApps Inside else -->"+appInfo.getIconPath());
                }
                saveAppIcon(context, appInfo, bitmapData);
                appInfo.setAppName(packageManager.getApplicationLabel(applicationInfo).toString());
                appInfos.add(appInfo);
            }
//            Intent mainIntent = new Intent(Intent.ACTION_MAIN);
//            mainIntent.addCategory(Intent.CATEGORY_HOME);
//            List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(mainIntent, 0);
//            for(ResolveInfo resolveInfo : resolveInfos) {
//                String packageName = resolveInfo.activityInfo.packageName;
//                AppInfo appInfo = new AppInfo();
//                Drawable drawable = resolveInfo.loadIcon(packageManager);
//                //Convert image to bytes
//                byte[] bitmapData = getDrawableBytes(getBitMapFromDrawable(drawable));
//                System.out.println("Package name is: "+packageName);
//                appInfo.setPackageName(packageName);
//                if (packageName.contains(".")) {
//                    appInfo.setIconPath(packageName.replaceAll("\\.", "") + ".png");
////                   System.out.println("getAllInstalledApps Inside if -->"+appInfo.getIconPath());
//                } else {
//                    appInfo.setIconPath(packageName + ".png");
////                    System.out.println("getAllInstalledApps Inside else -->"+appInfo.getIconPath());
//                }
//                saveAppIcon(context, appInfo, bitmapData);
//                appInfo.setAppName(packageManager.getApplicationLabel(resolveInfo.activityInfo.applicationInfo).toString());
//            }
            List<AppInfo> dialerApps = getDialerApps(packageManager, context);
            for (AppInfo dialerApp : dialerApps) {
                if (!appInfos.contains(dialerApp)) {
                    appInfos.add(dialerApp);
                }
            }

        } else {
            List<PackageInfo> packs = packageManager.getInstalledPackages(0);
            for (PackageInfo packageInfo : packs) {
                AppInfo appInfo = new AppInfo();
                Drawable drawable = packageInfo.applicationInfo.loadIcon(packageManager);
                byte[] bitmapData = getDrawableBytes(getBitMapFromDrawable(drawable));
                String packageName = packageInfo.packageName;
//                System.out.println("Package name is: "+packageName);
                appInfo.setPackageName(packageName);
                if (packageName.contains(".")) {
                    appInfo.setIconPath(packageName.replaceAll("\\.", "_") + ".png");
//                    System.out.println("getAllInstalledApps Inside if -->"+appInfo.getIconPath());
                } else {
                    appInfo.setIconPath(packageName + ".png");
//                    System.out.println("getAllInstalledApps Inside else -->"+appInfo.getIconPath());
                }
                saveAppIcon(context, appInfo, bitmapData);
                appInfo.setAppName(packageManager.getApplicationLabel(packageInfo.applicationInfo).toString());
                appInfos.add(appInfo);
            }
            List<AppInfo> dialerApps = getDialerApps(packageManager, context);
            for (AppInfo dialerApp : dialerApps) {
                if (!appInfos.contains(dialerApp)) {
                    appInfos.add(dialerApp);
                }
            }
        }
        return appInfos;
    }

    private static void saveAppIcon(Context context, AppInfo appInfo, byte[] bitmapData) {
        if (bitmapData != null) {
            try {
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
     * Convert Bitmap icon to bytes array
     *
     * @param bitmap
     * @return
     */
    private static byte[] getDrawableBytes(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
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
            byte[] bitmapData = getDrawableBytes(getBitMapFromDrawable(drawable));
            String packageName = resolveInfo.activityInfo.packageName;
            Log.d("Dialer app: ", packageName);
            appInfo.setPackageName(packageName);
            if (packageName.contains(".")) {
                appInfo.setIconPath(packageName.replaceAll("\\.", "") + ".png");
            } else {
                appInfo.setIconPath(packageName + ".png");
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

    private static Bitmap getBitMapFromDrawable(Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }


}
