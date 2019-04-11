package com.salamander.core.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.salamander.core.Utils;
import com.salamander.core.widget.SalamanderDialog;

public class DialogUtils {

    public static void showDialog(final Context context, String message) {
        showDialog(context, message, false);
    }

    public static void showDialog(final Context context, String message, final boolean finish) {
        if (message != null) {
            new SalamanderDialog(context)
                    .setDialogType(SalamanderDialog.DIALOG_ERROR)
                    .setMessage(Utils.textToHtml(message))
                    .setPositiveButton("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (finish)
                                ((Activity) context).finish();
                        }
                    })
                    .show();
        }
    }
}
