package krunal.com.example.errorreport;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Utility {

    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getIMEIno(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    @SuppressLint("MissingPermission")
    public static String getDeviceInfo() {

        List<String> lstDeviceInfo = new ArrayList<>();

        // TODO Auto-generated method stub
        String model = android.os.Build.MODEL;
        String serial = android.os.Build.SERIAL;
        String manufacturer = android.os.Build.MANUFACTURER;
        String androidSDK = String.valueOf(android.os.Build.VERSION.SDK_INT);
        String androidVersion = android.os.Build.VERSION.RELEASE;

        lstDeviceInfo = new ArrayList<>();
        lstDeviceInfo.add("MODEL: ".concat(model)+"\n");
        lstDeviceInfo.add("SERIAL: ".concat(serial)+"\n");
        lstDeviceInfo.add("MANUFACTURER: ".concat(manufacturer) +"\n");
        lstDeviceInfo.add("ANDROIDSDK: ".concat(androidSDK)+"\n");
        lstDeviceInfo.add("ANDROIDVERSION: ".concat(androidVersion)+"\n");
        return TextUtils.join("", lstDeviceInfo);
    }

    public static String getApplicationVersion(Context context) {
        String result = "";
        try {
            /*Version name*/
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            result = String.valueOf(pInfo.versionName);
            Log.i("current", "" + result);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(context, "Error in getApplicationVersion", Toast.LENGTH_SHORT).show();
        }
        return result;
    }
}
