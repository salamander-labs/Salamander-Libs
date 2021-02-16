package com.salamander.core.utils;

import android.app.Activity;
import android.content.Context;

import com.salamander.core.Utils;
import com.salamander.core.widget.SalamanderDialog;

public class DialogUtils {

    public static void showDialog(final Context context, String message) {
        showDialog(context, message, false);
    }

    public static void showDialog(final Context context, String message, final boolean finish) {
        if (message != null && context != null) {
            new SalamanderDialog(context)
                    .setDialogType(SalamanderDialog.DIALOG_ERROR)
                    .setMessage(Utils.textToHtml(message))
                    .setPositiveButton("OK", view -> {
                        if (finish)
                            ((Activity) context).finish();
                    })
                    .show();
        }
    }
}
