package com.salamander.salamander_logger;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.salamander.salamander_base_module.BuildConfig;
import com.salamander.salamander_base_module.Salamander;
import com.salamander.salamander_base_module.Utils;
import com.salamander.salamander_base_module.object.Tanggal;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import androidx.core.content.pm.PackageInfoCompat;

public class SalamanderExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Context context;
    //private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public SalamanderExceptionHandler(Context context) {
        this.context = context;
        try {
            Runtime.getRuntime().exec("logcat -c");
        } catch (Exception e) {
            Log.e("clearLog", e.toString());
        }
        context.startService(new Intent(context, ReportingService.class));
    }

    @Override
    public void uncaughtException(final Thread thread, final Throwable throwable) {
        throwable.printStackTrace();
        extractLogToFile();
        startActivity(throwable, writeLog(throwable));
        //Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
        //Thread.getDefaultUncaughtExceptionHandler().uncaughtException(thread, throwable);
        //uncaughtExceptionHandler.uncaughtException(thread, throwable);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

    private void startActivity(Throwable throwable, ErrorLog errorLog) {
        Intent intent = new Intent(context, CrashActivity.class);
        intent.putExtra("title", "GoSAM Force Closed");
        intent.putExtra("message", throwable.getMessage());
        intent.putExtra("logcat", errorLog.getLogCat());
        context.startActivity(intent);
    }

    private ErrorLog writeLog(Throwable throwable) {
        ErrorLog errorLog = new ErrorLog();
        StackTraceElement[] stackTraceElementList;
        if (throwable.getCause() != null)
            stackTraceElementList = throwable.getCause().getStackTrace();
        else stackTraceElementList = throwable.getStackTrace();
        String packageName = "com.salamander";
        String packageName2 = context.getPackageName();
        for (StackTraceElement stackTraceElement : stackTraceElementList) {
            if (stackTraceElement.toString().contains(packageName) || (stackTraceElement.toString().contains(packageName2)) &&
                    !stackTraceElement.toString().contains("<init>") &&
                    !stackTraceElement.getClassName().contains(BuildConfig.APPLICATION_ID) &&
                    !stackTraceElement.isNativeMethod()) {

                errorLog.setClassName(stackTraceElement.getClassName());
                errorLog.setMethodName(stackTraceElement.getMethodName());
                errorLog.setLineNumber(stackTraceElement.getLineNumber());
                errorLog.setException(throwable.getClass().getSimpleName());
                errorLog.setMessage((throwable.getCause() != null ? throwable.getCause().getLocalizedMessage() : throwable.getLocalizedMessage()));
                errorLog.setErrorDate(new Tanggal(new Date()));
                errorLog.setLogCat(LogUtils.readLogs());

                new LogSQLite(context).Post(errorLog);

                Log.e(Salamander.getInstance().getTAG(),
                        "=>\n==============================================================================================================================================" +
                                "\nClassName \t: " + stackTraceElement.getClassName() +
                                "\nMethodName \t: " + stackTraceElement.getMethodName() +
                                "\nLineNumber \t: " + stackTraceElement.getLineNumber() +
                                "\nLog \t\t: " + (throwable.getCause() != null ? throwable.getCause().getLocalizedMessage() : throwable.getLocalizedMessage()) +
                                "\n==============================================================================================================================================\n.");
                break;
            }
        }
        return errorLog;
    }

    private String extractLogToFile() {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e2) {
            Utils.showLog(e2);
        }
        String model = Build.MODEL;
        if (!model.startsWith(Build.MANUFACTURER))
            model = Build.MANUFACTURER + " " + model;

        // Make file name - file must be saved to external storage or it wont be readable by
        // the email app.
        String path = Environment.getExternalStorageDirectory() + "/";
        String fullName = path + context.getPackageName() + ".txt";

        // Extract to file.
        File file = new File(fullName);
        InputStreamReader reader = null;
        FileWriter writer = null;
        try {
            // For Android 4.0 and earlier, you will get all app's log output, so filter it to
            // mostly limit it to your app's output.  In later versions, the filtering isn't needed.
            String cmd = (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) ?
                    "logcat -d -e time MyApp:v dalvikvm:v System.err:v *:s" :
                    "logcat -d -e time";

            // get input stream
            Process process = Runtime.getRuntime().exec(cmd);
            reader = new InputStreamReader(process.getInputStream());

            int versionCode = 0;
            // write output stream
            if (info != null)
                versionCode = (int) PackageInfoCompat.getLongVersionCode(info);

            writer = new FileWriter(file);
            writer.write("Android version: " + Build.VERSION.SDK_INT + "\n");
            writer.write("Device: " + model + "\n");
            writer.write("App version: " + versionCode + "\n");

            char[] buffer = new char[10000];
            do {
                int n = reader.read(buffer, 0, buffer.length);
                if (n == -1)
                    break;
                writer.write(buffer, 0, n);
            } while (true);

            reader.close();
            writer.close();
        } catch (IOException e) {
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException e1) {
                    Utils.showLog(e1);
                }
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e1) {
                    Utils.showLog(e1);
                }

            // You might want to write a failure message to the log here.
            return null;
        }
        return fullName;
    }
}