package com.derric.quickbar;

import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager;

import com.derric.quickbar.constants.AppConstants;

public class QuickBarUtils {

    public static void setGravity(WindowManager.LayoutParams layoutParams, QuickBarManager.Settings settings) {
        if (settings.quickbarChooseSide.equals(AppConstants.RIGHT) && settings.quickbarChoosePosition.equals(AppConstants.TOP)) {
            //Display the quickbar on top right of the screen
            layoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
        } else if (settings.quickbarChooseSide.equals(AppConstants.RIGHT) && settings.quickbarChoosePosition.equals(AppConstants.CENTER)) {
            layoutParams.gravity = Gravity.RIGHT | Gravity.CENTER;
        } else if (settings.quickbarChooseSide.equals(AppConstants.RIGHT) && settings.quickbarChoosePosition.equals(AppConstants.BOTTOM)) {
            layoutParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        } else if (settings.quickbarChooseSide.equals(AppConstants.LEFT) && settings.quickbarChoosePosition.equals(AppConstants.TOP)) {
            layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        } else if (settings.quickbarChooseSide.equals(AppConstants.LEFT) && settings.quickbarChoosePosition.equals(AppConstants.CENTER)) {
            layoutParams.gravity = Gravity.LEFT | Gravity.CENTER;
        } else if (settings.quickbarChooseSide.equals(AppConstants.LEFT) && settings.quickbarChoosePosition.equals(AppConstants.BOTTOM)) {
            layoutParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
        }
    }

    public static void setGravityShowIcon(WindowManager.LayoutParams layoutParams, QuickBarManager.Settings settings) {
        if (settings.showIconChooseSide.equals(AppConstants.RIGHT) && settings.showIconChoosePosition.equals(AppConstants.TOP)) {
            //Display the showicon on top right of the screen
            layoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
        } else if (settings.showIconChooseSide.equals(AppConstants.RIGHT) && settings.showIconChoosePosition.equals(AppConstants.CENTER)) {
            layoutParams.gravity = Gravity.RIGHT | Gravity.CENTER;
        } else if (settings.showIconChooseSide.equals(AppConstants.RIGHT) && settings.showIconChoosePosition.equals(AppConstants.BOTTOM)) {
            layoutParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        } else if (settings.showIconChooseSide.equals(AppConstants.LEFT) && settings.showIconChoosePosition.equals(AppConstants.TOP)) {
            layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        } else if (settings.showIconChooseSide.equals(AppConstants.LEFT) && settings.showIconChoosePosition.equals(AppConstants.CENTER)) {
            layoutParams.gravity = Gravity.LEFT | Gravity.CENTER;
        } else if (settings.showIconChooseSide.equals(AppConstants.LEFT) && settings.showIconChoosePosition.equals(AppConstants.BOTTOM)) {
            layoutParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
        }
    }

    public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
