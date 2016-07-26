package com.android.jxcore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.jxcore.node.JXFunctions;
import io.jxcore.node.jxcore;

public class JXcore {
    private final jxcore mJXcore;
    private final JavaRegisteredFunctions mJavaMethods = new JavaRegisteredFunctions();
    private Runnable mOnInitialized = null;

    public JXcore(Context context) {
        mJXcore = new jxcore(context);
    }

    public void init(@Nullable final Runnable onInitialized) {
        //TODO: throw exception if already initialized
        mOnInitialized = onInitialized;

        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                mJXcore.addJXCustomFuctions(mJavaMethods);
                mJXcore.pluginInitialize();
                return null;
            }
        }.execute();
    }

    public void stop() {
        mJXcore.stopEngine();
        jxcore.handler.removeCallbacksAndMessages(null);
    }

    public void addJavaMethod(final String name, final JavaMethod callback) {
        //TODO: throw exception if already initialized
        mJavaMethods.mJavaCallbacks.put(name, new jxcore.JXcoreCallback() {
            @SuppressLint("NewApi")
            @Override
            public void Receiver(ArrayList<Object> params, String callbackId) {
                callback.execute(name, params);
            }
        });
    }

    public Set<String> getJavaMethodNames() {
        //TODO: need to optimize this call. mJavaMethods.mJavaCallbacks is redundant after registerFunction() was fired
        return mJavaMethods.mJavaCallbacks.keySet();
    }

    public void callJSMethod(final String id, final Object... args) {
        jxcore.CallJSMethod(id, args);
    }

    public interface JavaMethod {
        void execute(String name, ArrayList<Object> params);
    }

    private class JavaRegisteredFunctions implements JXFunctions {
        private final Map<String, jxcore.JXcoreCallback> mJavaCallbacks = new HashMap<String, jxcore.JXcoreCallback>() {{
            put("onInitialized", new jxcore.JXcoreCallback() {
                @SuppressLint("NewApi")
                @Override
                public void Receiver(ArrayList<Object> params, String callbackId) {
                    if (mOnInitialized != null) {
                        mOnInitialized.run();
                    }
                }
            });
        }};

        @Override
        public void registerFunction() {
            for (Map.Entry<String, jxcore.JXcoreCallback> val : mJavaCallbacks.entrySet()) {
                jxcore.RegisterMethod(val.getKey(), val.getValue());
            }
            // mJavaMethods.mJavaCallbacks.clear();
        }
    }
}