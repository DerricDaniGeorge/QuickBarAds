package com.derric.quickbar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.derric.quickbar.constants.AppConstants;
import com.derric.quickbar.models.AppInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LoadingActivity extends AppCompatActivity {

    public static List<AppInfo> appInfos;
    Future<List<AppInfo>> future;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
//        System.out.println("Inside loading...............");
        if (appInfos == null) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            future = executorService.submit(new Callable<List<AppInfo>>() {
                @Override
                public List<AppInfo> call() {
                    PackageManager packageManager = getPackageManager();
                    appInfos = QuickBarManager.getAllInstalledApps(packageManager, getApplicationContext());
                    return appInfos;
                }
            });
        }
        ArrayList<AppInfo> apps = new ArrayList<>();
        if (appInfos == null) {
            try {
                appInfos = future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        apps.addAll(appInfos);
        Intent mainActivity = new Intent(LoadingActivity.this,MainActivity.class);
        mainActivity.putExtra(AppConstants.APP_INFOS,apps);
        startActivity(mainActivity);
//        bundle.putSerializable(AppConstants.APP_INFOS, apps);
    }
}