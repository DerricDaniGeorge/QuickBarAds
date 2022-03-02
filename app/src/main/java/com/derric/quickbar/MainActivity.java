package com.derric.quickbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;

import com.derric.quickbar.constants.AppConstants;
import com.derric.quickbar.fragments.MainMenu;
import com.derric.quickbar.models.AppInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {
    public static List<AppInfo> appInfos;
    Future<List<AppInfo>> future;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //When the app is started, fetch the app details behind the scene.
        //As it has nothing to do with view, we can use java thread executor
        //If already fetched don't fetch again, This null check is added to avoid fetching data which happens when restoring the app after minimising it
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

        super.onCreate(savedInstanceState);
        //Set main activity layout
        setContentView(R.layout.activity_main);
        //To set a fragment to an activity, we have to first create a fragment transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //Create the fragment object
        MainMenu mainMenu = new MainMenu();
        //Add the main menu fragment to the activity layout
        transaction.add(R.id.container, mainMenu);
        transaction.commit();
        Bundle bundle = new Bundle();
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
        bundle.putSerializable(AppConstants.APP_INFOS, apps);
        mainMenu.setArguments(bundle);
    }
}