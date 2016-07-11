package com.coolapps;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import io.jxcore.node.jxcore;

/**
 * Created by Vanko7 on 31/03/2016.
 */
public class ServiceActivity extends Activity
{
    private static jxcore jx = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try
        {
            jx = new jxcore(this, JXCoreService.path, JXCoreService.readFileName);
            new InitAsync().execute();

            Log.i(JXCoreService.LOG_TAG, "Service Activity Created");
            finish();
        }
        catch (Exception ex)
        {
            Log.w(JXCoreService.LOG_TAG, "JXCore Initialization error");
            JXCoreService.LogException(this, ex);

            //Log.i(JXCoreService.LOG_TAG, "Service Activity Created");
            //finish();
        }
    }

    class InitAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            try
            {
                jx.pluginInitialize();
                Log.i(JXCoreService.LOG_TAG, "Plugin Initialized");
            }
            catch (Exception ex)
            {
                JXCoreService.LogException(ServiceActivity.this, ex);
            }

            return null;
        }

    }
}
