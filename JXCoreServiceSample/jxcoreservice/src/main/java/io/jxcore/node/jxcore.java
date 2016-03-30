// License information is available from LICENSE file

package io.jxcore.node;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONObject;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by oriharel on 5/3/15.
 */
public class jxcore {

  public static final String LOG_TAG = jxcore.class.getSimpleName();

  static {
    System.loadLibrary("jxcore");
  }

  public enum JXType {
    RT_Int32(1),
    RT_Double(2),
    RT_Boolean(3),
    RT_String(4),
    RT_JSON(5),
    RT_Buffer(6),
    RT_Undefined(7),
    RT_Null(8),
    RT_Error(9),
    RT_Function(10),
    RT_Object(11),
    RT_Unsupported(12);

    int val;
    private JXType(int n) {
      val = n;
    }

    public static JXType fromInt(int n) {
      switch(n) {
        case 1:
          return RT_Int32;
        case 2:
          return RT_Double;
        case 3:
          return RT_Boolean;
        case 4:
          return RT_String;
        case 5:
          return RT_JSON;
        case 6:
          return RT_Buffer;
        case 7:
          return RT_Undefined;
        case 8:
          return RT_Null;
        case 9:
          return RT_Error;
        default:
          return RT_Unsupported;
      }
    }
  }


  public native void setNativeContext(final Context context,
                                      final AssetManager assetManager);

  public native int loopOnce();

  public native void resetLoop();

  public native int loop();

  public native void startEngine();

  public native void prepareEngine(String home, String fileTree);

  public native void stopEngine();

  public native void defineMainFile(String content);

  public native void defineFile(String fileName, String content);

  public native String evalEngine(String script);

  public native int getType(long id);

  public native int getThreadId();

  public native double getDouble(long id);

  public native String getString(long id);

  public native int getInt32(long id);

  public native int getBoolean(long id);

  public native String convertToString(long id);

  public native void setTaskOptions(String taskOptions);

  public native long callCBString(String event_name, String param, int is_json);

  public jxcore() {
    Log.d(LOG_TAG, "jxcore constructor called");
  }


  public void Initialize(Context context, String home, String path, String mainFileName)
  {

    Log.d(LOG_TAG, "jxcore initialized with taskOptions: " + " home: " + home);
    Log.d(LOG_TAG, "JXCore Java thread id: "+Thread.currentThread().getId());

    StringBuilder assets = prepareAssets(context, path);

    try {
      prepareEngine(home, assets.toString());

      String mainFile = FileManager.readFile(context, mainFileName);

      String data = "process.cwd = function(){ return '" + home + "';};\n"
              + "NativeArgs = {};\n"
              + "NativeArgs.taskOptions = '" + "';\n"
              + "process.userPath ='" + context.getCacheDir().toString() + "';\n"
              + mainFile;

      defineMainFile(data);
    }
    catch (Exception ex) {
      Log.e(LOG_TAG, "Error preparing jxcore engine", ex);
    }

    startEngine();
    Log.d(LOG_TAG, "Initialize ended");

  }

  private StringBuilder prepareAssets(Context context, String path)
  {
    StringBuilder assets = new StringBuilder();
    assets.append("{");
    boolean first_entry = true;
    try {
      ZipFile zf = new ZipFile(
              context.getApplicationInfo().sourceDir);
      try {
        for (Enumeration<? extends ZipEntry> e = zf.entries(); e
                .hasMoreElements(); ) {
          ZipEntry ze = e.nextElement();
          String name = ze.getName();
          if (name.startsWith("assets" + path)) {
            if (first_entry)
              first_entry = false;
            else
              assets.append(",");
            int size = FileManager.aproxFileSize(context, name.substring(7));
            assets.append("\"" + name.substring(path.length() + 6) + "\":" + size);
          }
        }
      } finally {
        zf.close();
      }
    } catch (Exception e) {
    }
    assets.append("}");
    Log.i(LOG_TAG, "Prepare Assets: " + assets.toString());
    return assets;
  }
}