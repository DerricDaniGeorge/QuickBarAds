package com.derric.quickbar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import lombok.Getter;
import lombok.Setter;


//Extend FrameLayout, so that Quickbar act as a FrameLayout view
//Now the QuickBar itself is a frameLayout
//This class act as the floating quickbar
@Getter
@Setter
public class QuickBar extends LinearLayout{

    //default height
    public static final int DEFAULT_HEIGHT = ViewGroup.LayoutParams.WRAP_CONTENT;
    //default width
    public static final int DEFAULT_WIDTH = ViewGroup.LayoutParams.WRAP_CONTENT;
    private WindowManager.LayoutParams windowLayoutParams;
    private WindowManager quickBarWindowManager;

    //When the quickbar is created, perform all the below setups
    public QuickBar(@NonNull Context context) {
        super(context);
        quickBarWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //Set Window managers layout parameters on QuickBar creation.
        //Setting these are very important and core part of this application and without these settings
        //application won't work at all.
        windowLayoutParams = new WindowManager.LayoutParams();
        windowLayoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        windowLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        //TYPE_APPLICATION_OVERLAY -->Allows floating quickbar to get displayed over all other activities
        //except status bar on top or other critical views..
        windowLayoutParams.type = Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1 ?
                WindowManager.LayoutParams.TYPE_PRIORITY_PHONE : WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        //FLAG_NOT_FOCUSABLE --> Focus will be on what ever thing behind the view...Which means
        //user can interact with things behind the floating bar
        windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                //Allow window to extend outside of the screen
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        //Semi transparent
        windowLayoutParams.format = PixelFormat.TRANSLUCENT;
        //Display the quickbar on top right of the screen
        windowLayoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
        this.setBackgroundResource(R.drawable.green_panel);
        super.setOrientation(LinearLayout.VERTICAL);
        this.setOrientation(LinearLayout.VERTICAL);
    }

}
