package com.salamander.salamander_base_module;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.View;

import com.salamander.salamander_base_module.widget.SalamanderDialog;

public class DialogUtils {

    public static void showErrorMessage(final Context context, String message, final boolean finish) {
        showErrorNetwork(context, "Error", message, finish);
    }

    public static void showErrorNetwork(final Context context, String title, String message, final boolean finish, boolean leftAlign) {
        if (message != null || title != null) {
            final SalamanderDialog salamanderDialog = new SalamanderDialog(context);
            salamanderDialog.setDialogType(SalamanderDialog.DIALOG_ERROR);
            if (title != null)
                //salamanderDialog.setTitle(getMessage(title));
                salamanderDialog.setDialogTitle(title);
            if (message != null)
                //salamanderDialog.setMessage(Html.fromHtml(getMessage(message)));
                salamanderDialog.setMessage(Html.fromHtml(message));
            salamanderDialog.setPositiveButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finish)
                        ((Activity) context).finish();
                }
            });
            if (leftAlign)
                salamanderDialog.setAlign(SalamanderDialog.ALIGN_LEFT);
            salamanderDialog.show();
        }
    }

    public static void showErrorNetwork(final Context context, String title, String message, final boolean finish) {
        showErrorNetwork(context, title, message, finish, false);
    }

    private static String getMessage(String message) {
        String[] messages;
        String displayMessage = "";
        int indexStr = 0;
        messages = message.split(" ");
        for (String msg_split : messages) {
            if (msg_split.contains("http") || msg_split.contains("202.159.16.190"))
                messages[indexStr] = "Server";
            displayMessage += messages[indexStr] + " ";
            indexStr++;
        }

        if (displayMessage.contains("<h1>") && displayMessage.contains("</h1>"))
            displayMessage = displayMessage.substring(displayMessage.indexOf("<h1>"), displayMessage.indexOf("</h1>") + "</h1>" .length());
        return displayMessage;
    }
}
