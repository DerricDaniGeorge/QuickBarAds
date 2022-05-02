package com.derric.quickbar.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.derric.quickbar.R;
import com.derric.quickbar.models.AppInfo;

import java.io.File;
import java.util.List;

public class ChooseAppsAdapter extends RecyclerView.Adapter<ChooseAppsAdapter.MyViewHolder> {
    private final List<AppInfo> appInfos;
    private final Context context;
    private boolean anyChange;

    public ChooseAppsAdapter(List<AppInfo> appInfos, Context context) {
        this.appInfos = appInfos;
        this.context = context;
    }
    public boolean isAnyChange(){
        return this.anyChange;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView appIcon;
        private final TextView appName;
        private final CheckBox appSelection;

        public MyViewHolder(View itemLayout) {
            super(itemLayout);
            //You can access each views in layout here
            appIcon = itemLayout.findViewById(R.id.app_icon);
            appName = itemLayout.findViewById(R.id.app_name);
            appSelection = itemLayout.findViewById(R.id.app_selection_checkbox);
        }

        public ImageView getAppIcon() {
            return appIcon;
        }

        public TextView getAppName() {
            return appName;
        }

        public CheckBox getAppSelection() {
            return appSelection;
        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //For each viewholder add item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_apps_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //Set data to the views here
//        holder.getAppIcon().setImageDrawable(appInfos.get(position).getIcon());
//        byte[] imageBytes = appInfos.get(position).getIcon();
//        Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        AppInfo appInfo = appInfos.get(position);
        File filePath = context.getFileStreamPath(appInfo.getIconPath());
        Drawable icon = Drawable.createFromPath(filePath.toString());
//        ImageView iconView = new ImageView(context);
//        iconView.setImageDrawable(icon);
//        //Set icon's height and width
//        iconView.setLayoutParams(iconSize);

        holder.getAppName().setText(appInfo.getAppName());
        holder.getAppIcon().setImageDrawable(icon);
        CheckBox box = holder.getAppSelection();
//        box.setOnCheckedChangeListener(null);
        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                appInfo.setSelected(isChecked);
                anyChange = true;
//                if(!isChecked){
//                    selectAllApps = false;
//                }
            }
        });
//        if(selectAllApps){
//            box.setChecked(true);
//        }else{
        box.setChecked(appInfo.isSelected());

//        }


    }

    @Override
    public int getItemCount() {
        return appInfos.size();
    }

    public void selectAllItems() {
        for (AppInfo appInfo : appInfos) {
            appInfo.setSelected(true);
        }
//        this.selectAllApps = true;
        //Notify recyclerview data set is changed, so that the changes will be reflected in the list
        anyChange = true;
        notifyDataSetChanged();
    }

    public void deselectAllItems() {
        for (AppInfo appInfo : appInfos) {
            appInfo.setSelected(false);
        }
        anyChange = true;
//        this.selectAllApps = true;
        //Notify recyclerview data set is changed, so that the changes will be reflected in the list
        notifyDataSetChanged();
    }
}
