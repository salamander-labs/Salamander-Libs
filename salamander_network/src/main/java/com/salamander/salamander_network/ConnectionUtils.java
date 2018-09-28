package com.salamander.salamander_network;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.salamander.salamander_base_module.widget.SalamanderDialog;

public class ConnectionUtils {

    public ConnectionUtils() {}

    public interface OnTryAgain {
        void onTryAgain();
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
                salamanderDialog.setNegativeButton("Keluar", new View.OnClickListener() {
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