package com.coolapps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.util.Log;

import java.util.List;

import io.jxcore.node.jxcore;

public class JXCoreService extends Service {

    private static  String LOG_TAG = "JXCore Service";
    private static String path = "/app";
    private static String readFileName = "app/streaming.js";
    private static Context context;
    private static Intent serviceIntent;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this.getApplicationContext();

        jxcore jx_handler = new jxcore();
        jx_handler.setNativeContext(context, context.getAssets());
        jx_handler.Initialize(context, context.getFilesDir().getAbsolutePath(), path, readFileName);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    public static void start(Context _context, String _path, String _readFileName)
    {
        //check if process is already started
        ActivityManager activityManager = (ActivityManager) _context.getSystemService( ACTIVITY_SERVICE );
        List<RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for(int i = 0; i < procInfos.size(); i++)
        {
            String processName = procInfos.get(i).processName;
            if(processName.contains(":remote"))
            {
                Log.i(LOG_TAG, "Process is already running:" + processName);
                return;
            }
        }

        if (_path != null)
        {
            path = _path;
        }
        if (_readFileName != null)
        {
            readFileName = _readFileName;
        }

        serviceIntent = new Intent(_context, JXCoreService.class);
        _context.startService(serviceIntent);
    }
}

