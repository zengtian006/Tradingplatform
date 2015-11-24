package tp.ve.com.tradingplatform.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;

/**
 * Created by Zeng on 2015/11/23.
 */
public class VersionUtil {
    public static int getAppVersionCode(Context context) {
        int version = -1;
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static int getAppPrefInt(Context context, String prefName) {
        int result = -1;
        if (context != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (sharedPreferences != null) {
                result = sharedPreferences.getInt(
                        prefName, -1);
            }
        }
        return result;
    }

    public static void putAppPrefInt(Context context, String prefName, int value) {
        if (context != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putInt(prefName, value);
            if (Build.VERSION.SDK_INT >= 9) {
                edit.apply();
            } else {
                edit.commit();
            }
        }
    }
}
