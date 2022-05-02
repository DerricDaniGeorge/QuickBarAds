package com.derric.quickbar;

import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager;

import com.derric.quickbar.constants.AppConstants;
import com.derric.quickbar.models.AppInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class QuickBarUtils {

    public static void setGravity(WindowManager.LayoutParams layoutParams, QuickBarManager.Settings settings) {
        if (settings.quickbarChooseSide.equals(AppConstants.RIGHT) && settings.quickbarChoosePosition.equals(AppConstants.TOP)) {
            //Display the quickbar on top right of the screen
            layoutParams.gravity = Gravity.END | Gravity.TOP;
        } else if (settings.quickbarChooseSide.equals(AppConstants.RIGHT) && settings.quickbarChoosePosition.equals(AppConstants.CENTER)) {
            layoutParams.gravity = Gravity.END | Gravity.CENTER;
        } else if (settings.quickbarChooseSide.equals(AppConstants.RIGHT) && settings.quickbarChoosePosition.equals(AppConstants.BOTTOM)) {
            layoutParams.gravity = Gravity.END | Gravity.BOTTOM;
        } else if (settings.quickbarChooseSide.equals(AppConstants.LEFT) && settings.quickbarChoosePosition.equals(AppConstants.TOP)) {
            layoutParams.gravity = Gravity.START | Gravity.TOP;
        } else if (settings.quickbarChooseSide.equals(AppConstants.LEFT) && settings.quickbarChoosePosition.equals(AppConstants.CENTER)) {
            layoutParams.gravity = Gravity.START | Gravity.CENTER;
        } else if (settings.quickbarChooseSide.equals(AppConstants.LEFT) && settings.quickbarChoosePosition.equals(AppConstants.BOTTOM)) {
            layoutParams.gravity = Gravity.START | Gravity.BOTTOM;
        }
    }

    public static void setGravityShowIcon(WindowManager.LayoutParams layoutParams, QuickBarManager.Settings settings) {
        if (settings.showIconChooseSide.equals(AppConstants.RIGHT) && settings.showIconChoosePosition.equals(AppConstants.TOP)) {
            //Display the showicon on top right of the screen
            layoutParams.gravity = Gravity.END | Gravity.TOP;
        } else if (settings.showIconChooseSide.equals(AppConstants.RIGHT) && settings.showIconChoosePosition.equals(AppConstants.CENTER)) {
            layoutParams.gravity = Gravity.END | Gravity.CENTER;
        } else if (settings.showIconChooseSide.equals(AppConstants.RIGHT) && settings.showIconChoosePosition.equals(AppConstants.BOTTOM)) {
            layoutParams.gravity = Gravity.END | Gravity.BOTTOM;
        } else if (settings.showIconChooseSide.equals(AppConstants.LEFT) && settings.showIconChoosePosition.equals(AppConstants.TOP)) {
            layoutParams.gravity = Gravity.START | Gravity.TOP;
        } else if (settings.showIconChooseSide.equals(AppConstants.LEFT) && settings.showIconChoosePosition.equals(AppConstants.CENTER)) {
            layoutParams.gravity = Gravity.START | Gravity.CENTER;
        } else if (settings.showIconChooseSide.equals(AppConstants.LEFT) && settings.showIconChoosePosition.equals(AppConstants.BOTTOM)) {
            layoutParams.gravity = Gravity.START | Gravity.BOTTOM;
        }
    }

    public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static ArrayList<AppInfo> getUserSelectedApps(Set<String> userSelectedApps, ArrayList<AppInfo> allApps) {
        ArrayList<AppInfo> selectedApps = new ArrayList<>();
        if (userSelectedApps != null) {
            for (String packageName : userSelectedApps) {
                String[] split = packageName.split(":");
                for (AppInfo appInfo : allApps) {
                    if (appInfo.getPackageName().equals(split[0])) {
                        appInfo.setSelected(true);
                        if (split.length > 1) {
                            appInfo.setPosition(Integer.parseInt(split[1]));
                        }
                        selectedApps.add(appInfo);
                    }
                }
            }
        }
//        System.out.println("User selected apps called.............");
        sortAppsByPosition(selectedApps);
        return selectedApps;
    }

    public static void sortAppsByName(List<AppInfo> appInfos) {
        class AppNameAscendingComparator implements Comparator<AppInfo> {
            @Override
            public int compare(AppInfo o1, AppInfo o2) {
                return o1.getAppName().compareTo(o2.getAppName());
            }
        }
        Collections.sort(appInfos, new AppNameAscendingComparator());
    }

    public static void sortAppsByPosition(List<AppInfo> appInfos) {
        class SortByPositionComparator implements Comparator<AppInfo> {
            @Override
            public int compare(AppInfo o1, AppInfo o2) {
                return o1.getPosition() - o2.getPosition();
            }
        }
        Collections.sort(appInfos, new SortByPositionComparator());
    }
}
