package com.salamander.salamander_location;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidProcess;
import com.salamander.salamander_base_module.DialogUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SalamanderLocation {

    public static LocationSharedPreferenceManager getLocationManager(Context context) {
        return new LocationSharedPreferenceManager(context);
    }

    public static LocationInfo getLastLocation(Context context) {
        return new LocationSharedPreferenceManager(context).getLastLocation();
    }

    public static LocationInfo getLastMockLocation(Context context) {
        return new LocationSharedPreferenceManager(context).getLastMockPosition();
    }

    public static ArrayList<String> getListFakeGPSApp(Context context) {
        getListPackageWithMockPermission(context);
        List<AndroidProcess> processes = AndroidProcesses.getRunningProcesses();
        ArrayList<String> list_app = new ArrayList<>();
        ArrayList<String> list_app_running = new ArrayList<>();
        for (AndroidProcess process : processes) {
            list_app_running.add(process.name);
            for (String packageName : SalamanderLocation.getLocationManager(context).getListBlacklistApp()) {
                if (process.name.toLowerCase().contains(packageName)) {
                    list_app.add(process.name);
                }
            }
        }
        return list_app;
    }

    public static void setLastLocationAsMock(Context context) {
        LocationSharedPreferenceManager locationManager = getLocationManager(context);
        LocationInfo locationInfo = locationManager.getLastLocation();
        locationManager.setLastMockPosition(new LocationInfo(locationInfo.getLatitude(), locationInfo.getLongitude(), new Date().getTime(), locationInfo.getProvider()));
    }

    public static ArrayList<String> getListPackageWithMockPermission(Context context) {

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> processes = manager.getRunningServices(200);
        ArrayList<String> list_fake_gps_app = new ArrayList<>();
        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS);
                String[] requestedPermissions = packageInfo.requestedPermissions;
                if (requestedPermissions != null) {
                    for (String permission : requestedPermissions) {
                        String packageName = applicationInfo.packageName;
                        if (permission.equals("android.permission.ACCESS_MOCK_LOCATION") &&
                                (packageName.toLowerCase().contains("fake") ||
                                        packageName.toLowerCase().contains("mock") ||
                                        packageName.toLowerCase().contains("gps") ||
                                        packageName.toLowerCase().contains("location") ||
                                        packageName.toLowerCase().contains("lokasi") ||
                                        packageName.toLowerCase().contains("palsu"))
                                && !packageName.equals(context.getPackageName())) {
                            list_fake_gps_app.add(packageName);
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                //Log.e("ERROR->getPackageMock", e.toString());
                //Utils.showLog(BlackListAppManager.class.getSimpleName(), "getPackageMock", e.getMessage());
            }
        }
        for (ActivityManager.RunningServiceInfo service : processes) {
            if (service.service.getPackageName().toLowerCase().contains("fake") ||
                    service.service.getPackageName().toLowerCase().contains("mock") ||
                    service.service.getPackageName().toLowerCase().contains("gps") ||
                    service.service.getPackageName().toLowerCase().contains("location") ||
                    service.service.getPackageName().toLowerCase().contains("lokasi") ||
                    service.service.getPackageName().toLowerCase().contains("palsu")) {
                list_fake_gps_app.add(service.service.getPackageName());
            }
        }
        return list_fake_gps_app;
    }

    public static boolean isMockLocationEnabled(Context mContext) {
        boolean isMockLocation = false;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                AppOpsManager opsManager = (AppOpsManager) mContext.getSystemService(Context.APP_OPS_SERVICE);
                //isMockLocation = (opsManager.checkOp(AppOpsManager.OPSTR_MOCK_LOCATION, android.os.Process.myUid(), android.support.compat.BuildConfig.APPLICATION_ID) == AppOpsManager.MODE_ALLOWED);
            } else {
                isMockLocation = !android.provider.Settings.Secure.getString(mContext.getContentResolver(), "mock_location").equals("0");
            }
        } catch (Exception e) {
            return false;
        }
        return isMockLocation;
    }

    public static boolean isMockDisabled(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M && isMockLocationEnabled(context)) {
            DialogUtils.showErrorMessage(context, "Matikan setting lokasi palsu/mock location di Setting -> Developer Options -> Allow mock location", false);
            return false;
        } else {
            if (!isLocationValid(context)) {
                DialogUtils.showErrorMessage(context, "Tidak dapat mendapatkan lokasi.<br/>Silakan : <br/>- Matikan semua aplikasi lokasi palsu / fake gps / fake location<br/>- Matikan setting lokasi palsu/mock location <br/>- Restart GPS (matikan lalu hidupkan kembali GPS)<br/>- Tunggu beberapa saat lagi.<br/Buka Google Maps untuk melihat lokasi anda saat ini.", false);
                return false;
            }
        }
        return true;
    }

    private static boolean isLocationValid(Context context) {
        LocationSharedPreferenceManager session = getLocationManager(context);
        LocationInfo lastLocation = session.getLastLocation();
        LocationInfo lastMockPosition = session.getLastMockPosition();

        return lastLocation.getLatitude() != lastMockPosition.getLatitude() ||
                lastLocation.getLongitude() != lastMockPosition.getLongitude() ||
                !lastLocation.getProvider().equals(lastMockPosition.getProvider()) ||
                (lastLocation.getTime() - lastMockPosition.getTime() > 60 * 1000);
    }


}
