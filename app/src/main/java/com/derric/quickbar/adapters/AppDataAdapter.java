package com.derric.quickbar.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.derric.quickbar.R;
import com.derric.quickbar.models.AppInfo;

import java.io.File;
import java.util.List;

public class AppDataAdapter extends RecyclerView.Adapter<AppDataAdapter.MyViewHolder> {
    private final List<AppInfo> appInfos;
    private final Context context;

    public AppDataAdapter  (List<AppInfo> appInfos, Context context) {
        this.appInfos = appInfos;
        this.context = context;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView appIcon;
        private final TextView appName;
        private final CheckBox appSelection;
        public MyViewHolder(View itemLayout) {
            super(itemLayout);
            //You can access each views in layout here
            appIcon = itemLayout.findViewById(R.id.app_icon);
            appName=itemLayout.findViewById(R.id.app_name);
            appSelection=itemLayout.findViewById(R.id.app_selection_checkbox);
        }

        public ImageView getAppIcon() {
            return appIcon;
        }

        public TextView getAppName() {
            return appName;
        }
        public CheckBox getAppSelection(){
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
        File filePath = context.getFileStreamPath(appInfos.get(position).getIconPath());
        Drawable icon = Drawable.createFromPath(filePath.toString());
        holder.getAppIcon().setImageDrawable(icon);
        holder.getAppName().setText(appInfos.get(position).getAppName());
    }

    @Override
    public int getItemCount() {
        return appInfos.size();
    }
}
