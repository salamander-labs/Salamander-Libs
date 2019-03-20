package com.salamander.salamander_base_module.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.Nullable;
import android.text.method.KeyListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.salamander.salamander_base_module.R;
import com.salamander.salamander_base_module.Utils;

public class MandatoryEditText extends LinearLayout {

    private TextView tvCaption, tvMandatory;
    private int backgroundDrawable;
    private EditText txValue;
    private KeyListener keyListener;

    private boolean isMandatory;

    public MandatoryEditText(Context context) {
        super(context);
        init(context, null, 0);
    }

    public MandatoryEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public MandatoryEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
        init(context, attrs, defStyleAttr);
    }

    public void init(Context context,  @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MandatoryEditText, defStyleAttr, 0);

        String textCaption, textValue, textHint;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_mandatory_edittext, this);
        tvCaption = view.findViewById(R.id.met_caption);
        txValue = view.findViewById(R.id.met_text);
        tvMandatory = view.findViewById(R.id.met_mandatory);
        this.keyListener = txValue.getKeyListener();

        try {
            textCaption = typedArray.getString(R.styleable.MandatoryEditText_caption);
            textHint = typedArray.getString(R.styleable.MandatoryEditText_hint);
            textValue = typedArray.getString(R.styleable.MandatoryEditText_text);
            int captionColor = typedArray.getColor(R.styleable.MandatoryEditText_captionColor, Color.BLACK);
            int textColor = typedArray.getColor(R.styleable.MandatoryEditText_textColor, Color.BLACK);
            this.isMandatory = typedArray.getBoolean(R.styleable.MandatoryEditText_mandatory, false);

            txValue.setEnabled(typedArray.getBoolean(R.styleable.MandatoryEditText_enabled, true));
            txValue.setFocusable(typedArray.getBoolean(R.styleable.MandatoryEditText_focusable, true));
            txValue.setFocusableInTouchMode(typedArray.getBoolean(R.styleable.MandatoryEditText_focusableInTouchMode, true));
            txValue.setClickable(typedArray.getBoolean(R.styleable.MandatoryEditText_clickable, true));

            setEditable(typedArray.getBoolean(R.styleable.MandatoryEditText_enabled, true));
            tvCaption.setTextColor(captionColor);
            txValue.setTextColor(textColor);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                txValue.setBackground(typedArray.getDrawable(R.styleable.MandatoryEditText_background));
            else
                txValue.setBackground(typedArray.getDrawable(R.styleable.MandatoryEditText_background));
        } finally {
            typedArray.recycle();
        }

        setCaption(textCaption);
        setHint(textHint);
        setText(textValue);
        setMandatory(isMandatory);
    }

    public void setCaption(String text) {
        tvCaption.setText(text);
    }
    public void setText(String text) {
        txValue.setText(text);
    }
    public String getText() {
        return txValue.getText().toString();
    }
    public void setHint(String text) {
        txValue.setHint(text);
    }
    public void setMandatory(boolean isMandatory) {
        this.isMandatory = isMandatory;
        if (isMandatory)
            tvMandatory.setVisibility(VISIBLE);
        else tvMandatory.setVisibility(GONE);
    }
    public boolean isMandatory() {
        return this.isMandatory;
    }
    public EditText getEditText() {
        return this.txValue;
    }
    public void setEditable(boolean editable) {
        if (editable)
            txValue.setKeyListener(this.keyListener);
        else txValue.setKeyListener(null);
    }
    public boolean validate() {
        return !(this.isMandatory && Utils.isEmpty(txValue.getText().toString()));
    }
}
