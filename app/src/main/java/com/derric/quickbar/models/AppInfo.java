package com.derric.quickbar.models;

import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.widget.CheckBox;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppInfo implements Serializable {
    private String packageName;
    private String appName;
    //Icon path in device storage
    private String iconPath;
    private boolean selected;
}
