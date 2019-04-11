package com.salamander.network.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.core.content.ContextCompat;
import android.view.View;

import com.salamander.core.widget.SalamanderDialog;
import com.salamander.network.R;

public class ConnectionUtils {

    public ConnectionUtils() {
    }

    public interface OnTryAgain {
        void onTryAgain();
    }

    public static boolean isConnectedToInternet(final Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static boolean isConnectedToInternet(final Context context, final OnTryAgain onTryAgain) {
        return isConnectedToInternet(context, onTryAgain, false);
    }

    public static boolean isConnectedToInternet(final Context context, final OnTryAgain onTryAgain, boolean exit) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else {
            SalamanderDialog salamanderDialog = new SalamanderDialog(context)
                    .setDialogType(SalamanderDialog.DIALOG_ERROR)
                    .setMessage("Tidak terkoneksi ke Internet.\nSilakan check koneksi internet dan coba lagi.")
                    .setPositiveButtonText("Coba Lagi")
                    .setPositiveButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onTryAgain != null)
                                onTryAgain.onTryAgain();
                        }
                    });
            if (exit && context instanceof Activity) {
                salamanderDialog.setPositiveButtonColor(ContextCompat.getColor(context, R.color.green_okay));
                salamanderDialog.setNegativeButtonColor(ContextCompat.getColor(context, R.color.red_error));
                salamanderDialog.setNegativeButton("Exit", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((Activity) context).finish();
                    }
                });
                salamanderDialog.cancelable(false);
            }
            salamanderDialog.show();
            return false;
        }
    }
}
