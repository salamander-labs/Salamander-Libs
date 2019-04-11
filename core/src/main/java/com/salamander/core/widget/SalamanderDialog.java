package com.salamander.core.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.salamander.core.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class SalamanderDialog extends Dialog {

    public static final int DIALOG_ERROR = 1, DIALOG_INFORMATION = 2, DIALOG_WARNING = 3, DIALOG_CONFIRMATION = 4;
    public static final int ALIGN_CENTER = 0, ALIGN_LEFT = 1, ALIGN_RIGHT = 2;
    private Context context;
    private TextView tx_title, tx_message;
    private TextView bt_ok, bt_cancel;
    private View view_separator;

    public SalamanderDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        assert getWindow() != null;
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.salamander_dialog);
        tx_title = findViewById(R.id.tx_title);
        tx_message = findViewById(R.id.tx_message);
        bt_ok = findViewById(R.id.bt_ok);
        bt_cancel = findViewById(R.id.bt_cancel);
        view_separator = findViewById(R.id.view_separator);
        bt_cancel.setVisibility(View.GONE);
        bt_ok.setAllCaps(false);
        bt_cancel.setAllCaps(false);
        setDialogType(DIALOG_ERROR);
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
        tx_title.setText(titleId);
    }

    @Override
    public void setTitle(@Nullable CharSequence title) {
        super.setTitle(title);
        tx_title.setText(title);
    }

    public SalamanderDialog cancelable(boolean cancelable) {
        setCancelable(cancelable);
        return this;
    }

    public SalamanderDialog setDialogTitle(String title) {
        setTitle(title);
        return this;
    }

    public SalamanderDialog setDialogTitle(CharSequence title) {
        setTitle(title);
        return this;
    }

    public SalamanderDialog setMessage(@Nullable CharSequence message) {
        tx_message.setText(message);
        tx_message.setMovementMethod(new ScrollingMovementMethod());
        return this;
    }

    public SalamanderDialog setPositiveButton(String positiveButtonText, View.OnClickListener onClickListener) {
        setPositiveButtonText(positiveButtonText.replace(" \n", " ").replace("\n", " "));
        setPositiveButtonClickListener(onClickListener);
        return this;
    }

    public SalamanderDialog setPositiveButtonColor(int color) {
        bt_ok.setTextColor(color);
        return this;
    }

    public SalamanderDialog setNegativeButton(String negativeButtonText, View.OnClickListener onClickListener) {
        setNegativeButtonText(negativeButtonText.replace(" \n", " ").replace("\n", " "));
        setNegativeButtonClickListener(onClickListener);
        return this;
    }

    public SalamanderDialog setPositiveButtonText(String positiveButtonText) {
        bt_ok.setText(positiveButtonText);
        return this;
    }

    public SalamanderDialog setNegativeButtonColor(int color) {
        bt_cancel.setTextColor(color);
        return this;
    }

    public SalamanderDialog setPositiveButtonClickListener(final View.OnClickListener onClickListener) {
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null)
                    onClickListener.onClick(view);
                dismiss();
            }
        });
        return this;
    }

    public SalamanderDialog setNegativeButtonText(String negativeButtonText) {
        bt_cancel.setText(negativeButtonText);
        bt_cancel.setVisibility(View.VISIBLE);
        view_separator.setVisibility(View.VISIBLE);
            bt_ok.setBackground(null);
            bt_cancel.setBackground(null);
            bt_ok.setBackground(ContextCompat.getDrawable(context, R.drawable.button_positive_selector));
            bt_cancel.setBackground(ContextCompat.getDrawable(context, R.drawable.button_negative_selector));
        return this;
    }

    public SalamanderDialog setNegativeButtonClickListener(final View.OnClickListener onClickListener) {
        bt_cancel.setVisibility(View.VISIBLE);
        view_separator.setVisibility(View.VISIBLE);
            bt_ok.setBackground(null);
            bt_cancel.setBackground(null);
            bt_ok.setBackground(ContextCompat.getDrawable(context, R.drawable.button_positive_selector));
            bt_cancel.setBackground(ContextCompat.getDrawable(context, R.drawable.button_negative_selector));
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null)
                    onClickListener.onClick(view);
                dismiss();
            }
        });
        return this;
    }

    public SalamanderDialog setAlign(int messageAlign) {
        switch (messageAlign) {
            case ALIGN_LEFT:
                tx_message.setGravity(Gravity.START);
                break;
            case ALIGN_RIGHT:
                tx_message.setGravity(Gravity.END);
                break;
            default:
                tx_message.setGravity(Gravity.CENTER);
                break;
        }
        return this;
    }

    public SalamanderDialog setDialogType(int dialogType) {
        switch (dialogType) {
            case 1:
                tx_title.setText("Error");
                tx_title.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_round_rectangle_top_red));
                break;
            case 2:
                tx_title.setText("Information");
                tx_title.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_round_rectangle_top_blue));
                break;
            case 3:
                tx_title.setText("Warning");
                tx_title.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_round_rectangle_top_yellow));
                break;
            case 4:
                tx_title.setText("Confirmation");
                tx_title.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_round_rectangle_top_yellow));
                break;
            default:
                tx_title.setText("Error");
                tx_title.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_round_rectangle_top_red));
                break;
        }
        return this;
    }

    public SalamanderDialog setDismissListener(OnDismissListener onDismissListener) {
        this.setOnDismissListener(onDismissListener);
        return this;
    }
}