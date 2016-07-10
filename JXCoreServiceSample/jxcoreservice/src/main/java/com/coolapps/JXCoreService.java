package com.coolapps;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.content.ServiceConnection;
import android.content.ComponentName;

import java.util.List;

import io.jxcore.node.jxcore;

public class JXCoreService extends Service {

    public static String LOG_TAG = "JXCore Service";
    public static String path = "/app";
    public static String readFileName = "app/streaming.js";
    protected static ServiceConnection serverConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d(LOG_TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(LOG_TAG, "onServiceDisconnected");
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        //create activity for jxcore
        Intent intent = new Intent(this, ServiceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        Log.w(JXCoreService.LOG_TAG, "Service Created");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void start(Context _context, String _path, String _readFileName) {
        //check if process is already started
        ActivityManager activityManager = (ActivityManager) _context.getSystemService( ACTIVITY_SERVICE );
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for(int i = 0; i < procInfos.size(); i++)
        {
            String processName = procInfos.get(i).processName;
            if(processName.contains(":remoteJXCore"))
            {
                Log.i(LOG_TAG, "Process is already running:" + processName);
                return;
            }
        }

        Log.i(LOG_TAG, "Starting JXCore Service");

        if (_path != null) {
            path = _path;
        }
        if (_readFileName != null) {
            readFileName = _readFileName;
        }

        Intent serviceIntent = new Intent(_context, JXCoreService.class);
        _context.bindService(serviceIntent, serverConn, Context.BIND_AUTO_CREATE);
        _context.startService(serviceIntent);
    }
}
