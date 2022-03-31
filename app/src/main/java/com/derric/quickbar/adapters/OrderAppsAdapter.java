package com.derric.quickbar.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.derric.quickbar.R;
import com.derric.quickbar.helper.DragListener;
import com.derric.quickbar.helper.ItemActions;
import com.derric.quickbar.helper.Movable;
import com.derric.quickbar.models.AppInfo;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class OrderAppsAdapter extends RecyclerView.Adapter<OrderAppsAdapter.OrderAppItemHolder> implements Movable {
    private final List<AppInfo> userSelectedApps;
    private final Context context;
    private final DragListener dragListener;

    public OrderAppsAdapter(List<AppInfo> userSelectedApps, Context context, DragListener dragListener) {
        this.userSelectedApps = userSelectedApps;
        this.context = context;
        this.dragListener = dragListener;
    }


    public static class OrderAppItemHolder extends RecyclerView.ViewHolder implements ItemActions {
        private final ImageView appIcon;
        private final TextView appName;
        private final ImageView dragHandle;
        public final View itemLayout;

        public OrderAppItemHolder(View itemLayout) {
            super(itemLayout);
            this.itemLayout = itemLayout;
            //You can access each views in layout here
            appIcon = itemLayout.findViewById(R.id.app_icon);
            appName = itemLayout.findViewById(R.id.app_name);
            dragHandle = itemLayout.findViewById(R.id.dragHandle);
        }

        public ImageView getAppIcon() {
            return appIcon;
        }

        public TextView getAppName() {
            return appName;
        }

        public ImageView getDragHandle() {
            return dragHandle;
        }

        @Override
        public void onItemSelected() {
            itemLayout.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemLayout.setBackgroundColor(0);
        }
    }

    @NonNull
    @Override
    public OrderAppItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //For each viewholder add item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_apps_item, parent, false);
        return new OrderAppsAdapter.OrderAppItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAppItemHolder holder, int position) {
        AppInfo appInfo = userSelectedApps.get(position);
        File filePath = context.getFileStreamPath(appInfo.getIconPath());
        Drawable icon = Drawable.createFromPath(filePath.toString());
        holder.getAppName().setText(appInfo.getAppName());
        holder.getAppIcon().setImageDrawable(icon);
        holder.getDragHandle().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getActionMasked() == MotionEvent.ACTION_DOWN){
                    dragListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return userSelectedApps.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(userSelectedApps, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(userSelectedApps, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        userSelectedApps.remove(position);
        notifyItemRemoved(position);
    }
}
