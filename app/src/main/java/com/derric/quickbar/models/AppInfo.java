package com.derric.quickbar.models;

import android.graphics.drawable.Drawable;
import android.os.Parcelable;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppInfo implements Serializable {
    //Stores image as bytes
    private byte[] icon;
    private String packageName;
    private String appName;
    //Icon path in device storage
    private String iconPath;
}
