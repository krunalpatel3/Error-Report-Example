package krunal.com.example.errorreport;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class ErrorExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler defaultUEH;

    Context activity;
    String stacktrace, url, ip, mstringuploaduri, method, current;
    SQLiteDatabase db;
    Handler mHandler;
    Runnable runn;
    List<NameValuePair> parameters;
    private static String responseJSON;

    public ErrorExceptionHandler(Context context, String string) {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        this.activity = context;
        this.method = string;
    }

    public void uncaughtException(Thread t, Throwable e) {
        try {
            PackageInfo pInfo = activity.getPackageManager().getPackageInfo(
                    activity.getPackageName(), PackageManager.GET_META_DATA);
            current = String.valueOf(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException f) {
        }
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = "App version:-" + current + "\n" + result.toString();
        printWriter.close();

        StringBuilder stringBuilder = new StringBuilder();

        String AppInfo = Utility.getApplicationVersion(activity);
        String Device = Utility.getDeviceInfo();
        String IMEINo = Utility.getIMEIno(activity);

        stringBuilder.append("App Info\n");
        stringBuilder.append(AppInfo+"\n");
        stringBuilder.append("Device Info \n");
        stringBuilder.append(Device+ "\n");
        stringBuilder.append("IMEI No\n");
        stringBuilder.append(IMEINo+"\n");


        Log.e("AppInfo",AppInfo);
        Log.e("Device",Device);
        Log.e("IMEINo",IMEINo);


        String filename = "error" + System.nanoTime() + ".stacktrace";

        Log.e("Hi", "url != null");
        try {
            File rootPath = new File(Environment.getExternalStorageDirectory(),
                    ".fTouch");

            if (!rootPath.exists()) {
                rootPath.mkdirs();
            }
            Calendar c = Calendar.getInstance();

            SimpleDateFormat df = new SimpleDateFormat("dd");
            SimpleDateFormat dff = new SimpleDateFormat("MM");
            SimpleDateFormat tf = new SimpleDateFormat("hh");
            SimpleDateFormat tff = new SimpleDateFormat("mm");
            // System.out.println("Current time => " + c.getTime());
            String formattedDate = df.format(c.getTime());
            String formattedTime = tf.format(c.getTime());
            String formattedDate1 = dff.format(c.getTime());
            String formattedTime1 = tff.format(c.getTime());
            Random generator = new Random();
            int n = 100;
            n = generator.nextInt(n);
            String fname = "error_" + n + "_" + formattedDate + formattedDate1
                    + formattedTime + formattedTime1 + ".txt";
            File myFile = new File(rootPath, fname);
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(stacktrace);
            myOutWriter.close();
            fOut.close();
            Toast.makeText(activity, "Done writing SD 'mysdfile.txt'",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception eee) {
            Toast.makeText(activity, eee.getMessage(), Toast.LENGTH_SHORT)
                    .show();
        }
        String ecode = "";
        try {
            final SharedPreferences login = activity.getSharedPreferences(
                    "login", Context.MODE_PRIVATE);
            ecode = login.getString("ecode", "");
            Toast.makeText(activity, "SUCCESS", Toast.LENGTH_SHORT).show();
        } catch (Exception exp) {
            exp.getMessage();
            Toast.makeText(activity, "CATCH", Toast.LENGTH_SHORT).show();
        }

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.putExtra(Intent.EXTRA_EMAIL,
                new String[]{"rushangbhavani@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "error report App version:-" + current.concat(", EMPCODE-").concat(ecode));
        emailIntent.putExtra(Intent.EXTRA_TEXT,  "" + stringBuilder + "\n" + stacktrace);
//		emailIntent.setType("message/rfc822");
        emailIntent.setData(Uri.parse("mailto:"));
        activity.startActivity(Intent.createChooser(emailIntent,
                "Please Email Error Report:"));
//		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
//		emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		emailIntent.setType("plain/text");
//		emailIntent.setClassName("com.google.android.gm",
//				"com.google.android.gm.ComposeActivityGmail");
//		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
//				new String[] { "rcap@focusbspl.com" });
//		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
//				"error report");
//		emailIntent
//				.putExtra(android.content.Intent.EXTRA_TEXT, "" + stacktrace);
//		activity.startActivity(emailIntent);
        Toast.makeText(activity, "Send Error Report to online using email.",
                Toast.LENGTH_SHORT).show();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
        // sendToServer(stacktrace, filename);

        StackTraceElement[] arr = e.getStackTrace();
        String report = e.toString() + "\n\n";
        report += "--------- Stack trace ---------\n\n";
        for (int i = 0; i < arr.length; i++) {
            report += "    " + arr[i].toString() + "\n";
        }
        report += "-------------------------------\n\n";

        report += "--------- Cause ---------\n\n";
        Throwable cause = e.getCause();
        if (cause != null) {
            report += cause.toString() + "\n\n";
            arr = cause.getStackTrace();
            for (int i = 0; i < arr.length; i++) {
                report += "    " + arr[i].toString() + "\n";
            }
        }
        report += "-------------------------------\n\n";

        defaultUEH.uncaughtException(t, e);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) activity
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    // private void sendToServer(String stacktrace, String filename) {
    // this.stacktrace = stacktrace;
    // url = "datetime=" + DateFormat.getDateTimeInstance().format(new Date());
    // url = url + "&method=" + "";
    // url = url + "&errormessage=" + "";
    // parameters = new ArrayList<NameValuePair>();
    // parameters.add(new BasicNameValuePair("datetime", DateFormat
    // .getDateTimeInstance().format(new Date())));
    // parameters.add(new BasicNameValuePair("method", method));
    // parameters.add(new BasicNameValuePair("errormessage", stacktrace));
    // final SharedPreferences Ipconfig = activity.getSharedPreferences(
    // "Ipconfig", Serverconfiguration.MODE_PRIVATE);
    // ip = Ipconfig.getString("ip", "182.72.28.182");
    // // mstringuploaduri = "http://" + ip
    // // + "/services/appServices.asmx/"
    // // +
    // // URLEncoder.encode(cur.getString(cur.getColumnIndex("url")));
    // mstringuploaduri = "http://" + ip
    // + "/services/appServices.asmx/errorLogInsert?";
    // // new CallWebService_upload().execute();
    // // mHandler = new Handler();
    // // final Handler handler = new Handler();
    // // runn = new Runnable() {
    // //
    // // public void run() {
    // // db = activity.openOrCreateDatabase("db_focus", activity.MODE_APPEND,
    // // null);
    // // db.execSQL("insert into tbl_offlineapi values(?,'errorLogInsert?','"
    // // + url + "',datetime());");
    // // }
    // // };
    // // handler.postDelayed(runn, 40);
    // activity.runOnUiThread(new Runnable() {
    // @Override
    // public void run() {
    // db = activity.openOrCreateDatabase("db_focus",
    // activity.MODE_APPEND, null);
    // db.execSQL("insert into tbl_offlineapi values(?,'errorLogInsert?','"
    // + url + "',datetime());");
    // }
    // });
    // }

//    public class CallWebService_upload extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected Void doInBackground(Void... params) {
//            // Call Webservice for Get Menus
//            WebServiceCall webServiceCall = new WebServiceCall();
//
//            responseJSON = webServiceCall.makeServiceCall(mstringuploaduri,
//                    parameters, 0);
//            Log.i("json", "" + responseJSON);
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//
//            try {
//                JSONArray mJsonArray = new JSONArray(responseJSON);
//                JSONObject mJsonObject = new JSONObject();
//                for (int j = 0; j < mJsonArray.length(); j++) {
//                    mJsonObject = mJsonArray.getJSONObject(j);
//                }
//
//                if (mJsonObject.getString("SUCCESS").equalsIgnoreCase("1")) {
//                    Toast.makeText(activity, "Successfully to insert",
//                            Toast.LENGTH_SHORT).show();
//                } else {
//                    // progress.dismiss();
//                    // Toast.makeText(getApplicationContext(), "Fail to insert",
//                    // Toast.LENGTH_SHORT).show();
//                    // i++;
//                }
//
//            } catch (JSONException e) {
//                // TODO Auto-generated catch block
//                // Toast.makeText(getApplicationContext(), "Login faild.",
//                // Toast.LENGTH_SHORT).show();
//                // progress.dismiss();
//                // Toast.makeText(getApplicationContext(), "Fail to insert",
//                // Toast.LENGTH_SHORT).show();
//                // i++;
//                e.printStackTrace();
//            } catch (NullPointerException f) {
//                // TODO Auto-generated catch block
//                f.printStackTrace();
//            }
//
//            // Toast.makeText(getApplicationContext(), responseJSON,
//            // Toast.LENGTH_LONG).show();
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//    }
//
//    public String getAppLable(Context pContext) {
//        PackageManager lPackageManager = pContext.getPackageManager();
//        ApplicationInfo lApplicationInfo = null;
//        try {
//            lApplicationInfo = lPackageManager.getApplicationInfo(
//                    pContext.getApplicationInfo().packageName, 0);
//        } catch (final PackageManager.NameNotFoundException e) {
//        }
//        return (String) (lApplicationInfo != null ? lPackageManager
//                .getApplicationLabel(lApplicationInfo) : "Unknown");
//    }
}
