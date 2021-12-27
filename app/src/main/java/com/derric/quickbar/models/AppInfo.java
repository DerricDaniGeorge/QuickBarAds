package com.derric.quickbar.models;

import android.graphics.drawable.Drawable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppInfo {
    private Drawable icon;
    private String packageName;
    private String appName;
}
