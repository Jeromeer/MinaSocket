package app.socketlib.com.library.utils;

import android.util.Log;

/**
 * @author：JianFeng
 * @date：2017/10/31 15:46
 * @description：
 */
public class LogUtil {
    private static final boolean isDebug = true;
    private static final String TAG = "socketlib--->LOG:";

    public static void w(String logMessage) {
        if (isDebug) {
            Log.w(TAG, logMessage);
        }
    }


    public static void e(String logMessage) {
        if (isDebug) {
            Log.e(TAG, logMessage);
        }
    }

    public static void i(String logMsg){
        if (isDebug) {
            Log.i(TAG, logMsg);
        }
    }

}
