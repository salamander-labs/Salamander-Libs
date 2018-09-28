package com.salamander.salamander_base_module;

import com.salamander.salamander_base_module.object.Tanggal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtils {

    public GregorianCalendar dateToGregorianCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static String dateToString(String format, Tanggal tanggal) {
        return new SimpleDateFormat(format, Locale.getDefault()).format(tanggal.getDate());
    }
    public static String dateToString(String format, Tanggal tanggal, Locale locale) {
        return new SimpleDateFormat(format, locale).format(tanggal.getDate());
    }
    public static String dateToString(String format, Date date) {
        return new SimpleDateFormat(format, Locale.getDefault()).format(date);
    }
    public static String dateToString(String format, Date date, Locale locale) {
        return new SimpleDateFormat(format, locale).format(date);
    }

    public static Date stringToDate(String format, String dateStr) {
        try {
            return new SimpleDateFormat(format, Locale.getDefault()).parse(dateStr);
        } catch (Exception e) {
            //FileUtil.writeExceptionLog(context, TermsOfPaymentSQLite.class.getSimpleName() + " => Insert => ", e);
            return null;
        }
    }
}
