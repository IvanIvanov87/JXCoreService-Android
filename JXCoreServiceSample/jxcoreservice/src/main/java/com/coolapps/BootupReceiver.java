package com.coolapps;

/**
 * Created by Vanko7 on 29/03/2016.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent)
    {
        JXCoreService.start(context, null, null);
    }
}
