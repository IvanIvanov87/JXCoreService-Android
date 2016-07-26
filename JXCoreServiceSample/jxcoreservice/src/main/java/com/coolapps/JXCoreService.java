package com.coolapps;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.content.ServiceConnection;
import android.content.ComponentName;

import com.android.jxcore.JXcore;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import io.jxcore.node.jxcore;

public class JXCoreService extends Service implements Thread.UncaughtExceptionHandler {
    public static final String ACTION_STOP_JXCORE_SERVICE = "jxcore.action.UNBIND";
    public static final String ACTION_JXCORE_SERVICE_STARTED = "jxcore.action.STARTED";

    private static final String TAG = "JXCoreService";

    protected JXcore mJxCore = null;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_STOP_JXCORE_SERVICE:
                    stopJXCoreService();
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        Thread.setDefaultUncaughtExceptionHandler(this);

        super.onCreate();
        init(getApplicationContext());
        registerReceiver(mReceiver, new IntentFilter() {{
            addAction(ACTION_STOP_JXCORE_SERVICE);
        }});
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    //@Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void init(final Context context) {
        if (mJxCore == null) {
            mJxCore = new JXcore(context);

            // Example on how to add Java functions in JXCore
            /*
            mJxCore.addJavaMethod("out", new JXcore.JavaMethod() {
                @Override
                public void execute(String name, ArrayList<Object> params) {
                    if (params.size() == 3) {
                        try { // if you need you can add here your own callbacks
                            Log.d(TAG, "from JS to Java: " + params.get(1) + " " + params.get(2));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e(TAG, "unsuitable method signature");
                    }
                }
            });
            */

            // it runs when jxcore will be initialized
            mJxCore.init(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "jxcore fully initialized");
                    // Example on how to send something from Java to JXCore
                    //mJxCore.callJSMethod("javaFunctions", mJxCore.getJavaMethodNames().toArray());

                    // Example on how to broadcast a message in Java when JXCore is started
                    context.sendBroadcast(new Intent(ACTION_JXCORE_SERVICE_STARTED));
                }
            });
        }
    }

    public static void LogException(ContextWrapper context, Throwable ex){
        SharedPreferences.Editor editor = context.getSharedPreferences("prefs.db", MODE_PRIVATE).edit();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String message = "Message: " + ex.getMessage() + " StackTrace: " + sw.toString();
        editor.putString("uncaughtError", message);
        editor.commit();
        Log.i(TAG, "Exception logged");
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        LogException(this, ex);
    }

    /**
     * JXCore requires a special care to stopping it
     */
    protected void stopJXCoreService() {
        mJxCore.stop();
        stopSelf();
        System.runFinalization();
        System.exit(0);
    }
}

