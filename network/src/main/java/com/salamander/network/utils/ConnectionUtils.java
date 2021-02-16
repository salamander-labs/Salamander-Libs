package com.salamander.network.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.core.content.ContextCompat;

import com.salamander.core.widget.SalamanderDialog;
import com.salamander.network.R;

public class ConnectionUtils {

    public enum CONNECTION_TYPE {
        MOBILE,
        WIFI,
        OTHER,
        NOT_CONNECTED
    }

    public interface OnTryAgain {
        void onTryAgain();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return (getConnectionType(networkCapabilities) == CONNECTION_TYPE.WIFI || getConnectionType(networkCapabilities) == CONNECTION_TYPE.MOBILE);
        } else {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
    }

    public static CONNECTION_TYPE getConnectionType(NetworkCapabilities networkCapabilities) {
        if (networkCapabilities == null)
            return CONNECTION_TYPE.NOT_CONNECTED;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                return CONNECTION_TYPE.WIFI;
            else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                return CONNECTION_TYPE.MOBILE;
            else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
                return CONNECTION_TYPE.OTHER;
        }
        return CONNECTION_TYPE.NOT_CONNECTED;
    }

    public static boolean isConnected(final Context context, final OnTryAgain onTryAgain) {
        return isConnected(context, onTryAgain, false);
    }

    public static boolean isConnected(final Context context, final OnTryAgain onTryAgain, boolean exit) {
        if (isConnected(context))
            return true;
        else {
            SalamanderDialog salamanderDialog = new SalamanderDialog(context)
                    .setDialogType(SalamanderDialog.DIALOG_ERROR)
                    .setMessage("Tidak terkoneksi ke Internet.\nSilakan check koneksi internet dan coba lagi.")
                    .setPositiveButtonText("Coba Lagi")
                    .setPositiveButtonClickListener(v -> {
                        if (onTryAgain != null)
                            onTryAgain.onTryAgain();
                    });
            if (exit && context instanceof Activity) {
                salamanderDialog.setPositiveButtonColor(ContextCompat.getColor(context, R.color.green_okay));
                salamanderDialog.setNegativeButtonColor(ContextCompat.getColor(context, R.color.red_error));
                salamanderDialog.setNegativeButton("Exit", v -> ((Activity) context).finish());
                salamanderDialog.cancelable(false);
            }
            salamanderDialog.show();
            return false;
        }
    }
}
