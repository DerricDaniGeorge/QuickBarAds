package com.derric.quickbar.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.derric.quickbar.R;
import com.derric.quickbar.models.AppInfo;

import java.util.List;

public class ChooseAppAdapter extends BaseAdapter {
    private final List<AppInfo> appInfos;

    public ChooseAppAdapter(List<AppInfo> appInfos){
        this.appInfos=appInfos;
    }
    @Override
    public int getCount() {
        return appInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return appInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_apps_item, parent, false);
//        ImageView appIcon = itemLayout.findViewById(R.id.app_icon);
//        TextView appName=itemLayout.findViewById(R.id.app_name);
//        CheckBox appSelection=itemLayout.findViewById(R.id.app_selection_checkbox);
//        appIcon.setImageDrawable(appInfos.get(position).getIcon());
//        appName.setText(appInfos.get(position).getAppName());
        return itemLayout;
    }
}
