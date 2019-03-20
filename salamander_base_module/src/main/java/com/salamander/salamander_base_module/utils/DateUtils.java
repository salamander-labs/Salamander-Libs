package com.salamander.salamander_base_module.utils;

import com.salamander.salamander_base_module.object.Tanggal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtils {

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
            if (dateStr == null)
                return new Date(0);
            else return new SimpleDateFormat(format, Locale.getDefault()).parse(dateStr);
        } catch (Exception e) {
            //FileUtil.writeExceptionLog(context, TermsOfPaymentSQLite.class.getSimpleName() + " => Insert => ", e);
            return null;
        }
    }

    public static int calcDays(Date fromDate, Date toDate, int initialDays) {
        Calendar cMulai = Calendar.getInstance(),
                cSelesai = Calendar.getInstance();
        cMulai.setTime(fromDate);
        cSelesai.setTime(toDate);
        cMulai.set(cMulai.get(Calendar.YEAR), cMulai.get(Calendar.MONTH), cMulai.get(Calendar.DAY_OF_MONTH));
        cSelesai.set(cSelesai.get(Calendar.YEAR), cSelesai.get(Calendar.MONTH), cSelesai.get(Calendar.DAY_OF_MONTH));
        long diff = cSelesai.getTime().getTime() - cMulai.getTime().getTime();
        long diffDays = TimeUnit.MILLISECONDS.toDays(diff);
        return (int) diffDays + initialDays;
    }

    public static int calcWorkDays(Date fromDate, Date toDate) {

        int workDays = 0;

        fromDate = stringToDate(Tanggal.FORMAT_DATE, dateToString(Tanggal.FORMAT_DATE, fromDate));
        toDate = stringToDate(Tanggal.FORMAT_DATE, dateToString(Tanggal.FORMAT_DATE, toDate));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

        if (fromDate != null && toDate != null)
            do {
                workDays += 1;
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                fromDate.setTime(calendar.getTimeInMillis());
            } while (fromDate.getTime() <= toDate.getTime());
        return workDays;
    }


    public GregorianCalendar dateToGregorianCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }
}