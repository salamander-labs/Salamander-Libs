package com.salamander.core.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CurrencyUtils {

    public static double CalcTotal(EditText... editText) {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
        String groupingSeparator = Character.toString(decimalFormatSymbols.getGroupingSeparator());
        String decimalSeparator = Character.toString(decimalFormatSymbols.getDecimalSeparator());
        String[] datas = new String[editText.length];
        double total = 0.0;
        for (int i = 0; i < editText.length; i++) {
            datas[i] = editText[i].getText().toString().replaceAll(groupingSeparator, "");

            if (TextUtils.isEmpty(datas[i]) || datas[i].equals("0" + decimalSeparator))
                total += 0.0;
            else total += Double.parseDouble(datas[i]);
        }
        return total;
    }

    public static double CalcTotal(int jmlHari, EditText... editText) {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
        String groupingSeparator = Character.toString(decimalFormatSymbols.getGroupingSeparator());
        String decimalSeparator = Character.toString(decimalFormatSymbols.getDecimalSeparator());
        String[] datas = new String[editText.length];
        double total = 0.0;
        for (int i = 0; i < editText.length; i++) {
            datas[i] = editText[i].getText().toString().replaceAll("[^0-9" + decimalSeparator + "]", "");
            if (TextUtils.isEmpty(datas[i]) || datas[i].equals("0" + decimalSeparator))
                total += 0.0;
            else total += Double.parseDouble(datas[i]) * jmlHari;
        }
        return total;
    }

    public static void FormatNumberDecimal(Editable s, EditText e) {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
        String groupingSeparator = Character.toString(decimalFormatSymbols.getGroupingSeparator());
        String decimalSeparator = Character.toString(decimalFormatSymbols.getDecimalSeparator());
        if (!s.toString().equals("")) {

            int start, end, cp, sel;
            String price;

            if (s.toString().startsWith("0" + decimalSeparator))
                price = s.toString();
            else
                price = formatNumber(Double.valueOf(s.toString().replaceAll(groupingSeparator, "")));

            start = e.getText().length();
            cp = e.getSelectionStart();
            if (!s.toString().trim().equals(price))
                e.setText(price);
            end = e.getText().length();
            sel = cp + (end - start);
            if (sel > 0 && sel <= e.getText().length()) {
                e.setSelection(sel);
            } else {
                // place cursor at the end?
                e.setSelection(e.getText().length());
            }
        }
    }

    public static String formatNumber(double d) {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat numberFormat = new DecimalFormat("###,###,###.##", decimalFormatSymbols);
        return numberFormat.format(d);
    }

    public static String formatNumber(String format, double d) {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat numberFormat = new DecimalFormat(format, decimalFormatSymbols);
        return numberFormat.format(d);
    }

    public static String formatCurrency(String currencySymbol, double d) {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat numberFormat = new DecimalFormat("###,###,###.##", decimalFormatSymbols);
        return currencySymbol + numberFormat.format(d);
    }

    public static String formatCurrencyWithSign(String currencySymbol, double d) {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat numberFormat = new DecimalFormat("###,###,###.##", decimalFormatSymbols);
        Locale.setDefault(Locale.US);
        if (d < 0)
            return " - " + currencySymbol + numberFormat.format(d * -1);
        else return " + " + currencySymbol + numberFormat.format(d);
    }

    public static String FloatToStr(float number, String formatNumber) {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat numberFormat = new DecimalFormat(formatNumber);
        return numberFormat.format(number);
    }

    public static double getDouble(EditText e) {
        double value;
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
        String groupingSeparator = Character.toString(decimalFormatSymbols.getGroupingSeparator());
        String decimalSeparator = Character.toString(decimalFormatSymbols.getDecimalSeparator());
        String txt = e.getText().toString().replaceAll(groupingSeparator, "").trim();
        if (txt.endsWith(decimalSeparator))
            txt = txt + "0";
        if (txt.equals(""))
            value = 0.0;
        else
            value = Double.parseDouble(txt);

        return value;
    }

    public static double getDouble(TextView e) {
        double value;
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
        String groupingSeparator = Character.toString(decimalFormatSymbols.getGroupingSeparator());
        String txt = e.getText().toString().replace("Rp.", "").replaceAll("[^0-9.]", "").replaceAll(groupingSeparator, "").trim();
        if (txt.equals(""))
            value = 0.0;
        else
            value = Double.parseDouble(txt);

        return value;
    }

    public static int getInt(EditText e) {
        int value;
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
        String groupingSeparator = Character.toString(decimalFormatSymbols.getGroupingSeparator());
        String txt = e.getText().toString().replaceAll(groupingSeparator, "");
        if (txt.equals(""))
            value = 0;
        else
            value = Integer.parseInt(txt);

        return value;
    }
}