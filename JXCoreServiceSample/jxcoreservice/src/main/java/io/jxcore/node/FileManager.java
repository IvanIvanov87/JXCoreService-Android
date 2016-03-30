// License information is available from LICENSE file

package io.jxcore.node;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by oriharel on 5/3/15.
 */
@SuppressLint("DefaultLocale")
public class FileManager {
    public static String readFile(Context context, String location) {
        return readFile(context, location, "UTF-8");
    }

    public static String readFile(Context context, String location, String encoding) {
        StringBuilder sb = new StringBuilder();
        try {
            AssetManager asm = context.getAssets();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    asm.open(location), encoding));

            String str = br.readLine();
            while (str != null) {
                sb.append(str + "\n");
                str = br.readLine();
            }

            br.close();
        } catch (IOException e) {
            Log.w("jxcore-FileManager", "readfile failed");
            e.printStackTrace();
            return null;
        }

        return sb.toString();
    }

    public static int aproxFileSize(Context context, String location) {
        int size = 0;
        try {
            AssetManager asm = context.getAssets();
            InputStream st = asm.open(location, AssetManager.ACCESS_UNKNOWN);
            size = st.available();
            st.close();
        } catch (IOException e) {
            Log.w("jxcore-FileManager", "aproxFileSize failed");
            e.printStackTrace();
            return 0;
        }

        return size;
    }
}