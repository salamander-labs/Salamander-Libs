package com.salamander.salamander_base_module.object;

import android.os.Parcel;
import android.os.Parcelable;

import com.salamander.salamander_base_module.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Tanggal implements Parcelable {

    public static final String FORMAT_DATE = "yyyy-MM-dd";
    public static final String FORMAT_DATETIME_FULL = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATETIME_NO_SEC = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_UI = "EEEE, dd MMMM yyyy";
    public static final String FORMAT_UI_NO_DAY = "dd MMMM yyyy";
    public static final String FORMAT_TIME_FULL = "HH:mm:ss";
    public static final String FORMAT_TIME_NO_SECOND = "HH:mm";

    private long tglMilis = 0;

    public Tanggal(){}

    public Tanggal(Date defaultDate){
        set(defaultDate);
    }

    public Tanggal(long defaultDateMilis){
        set(defaultDateMilis);
    }

    protected Tanggal(Parcel in) {
        tglMilis = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(tglMilis);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Tanggal> CREATOR = new Creator<Tanggal>() {
        @Override
        public Tanggal createFromParcel(Parcel in) {
            return new Tanggal(in);
        }

        @Override
        public Tanggal[] newArray(int size) {
            return new Tanggal[size];
        }
    };

    public void set(long tglMilis) {
        this.tglMilis = tglMilis;
    }
    public void set(Date tglDate) {
        this.tglMilis = tglDate.getTime();
    }
    public void set(String dateFormat, String tglString) {
        this.tglMilis = DateUtils.stringToDate(dateFormat, tglString).getTime();
    }

    public void setDate(Date tglDate) {
        Calendar currentCalendar = Calendar.getInstance();
        Calendar newCalendar = Calendar.getInstance();
        currentCalendar.setTime(getDate());
        newCalendar.setTime(tglDate);
        currentCalendar.set(Calendar.YEAR, newCalendar.get(Calendar.YEAR));
        currentCalendar.set(Calendar.MONTH, newCalendar.get(Calendar.MONTH));
        currentCalendar.set(Calendar.DAY_OF_MONTH, newCalendar.get(Calendar.DAY_OF_MONTH));
        currentCalendar.set(Calendar.DAY_OF_MONTH, newCalendar.get(Calendar.DAY_OF_MONTH));
        set(currentCalendar.getTime());
    }
    public void setTime(Date tglDate) {
        Calendar currentCalendar = Calendar.getInstance();
        Calendar newCalendar = Calendar.getInstance();
        currentCalendar.setTime(getDate());
        newCalendar.setTime(tglDate);
        currentCalendar.set(Calendar.HOUR_OF_DAY, newCalendar.get(Calendar.HOUR_OF_DAY));
        currentCalendar.set(Calendar.MINUTE, newCalendar.get(Calendar.MINUTE));
        currentCalendar.set(Calendar.SECOND, newCalendar.get(Calendar.SECOND));
        currentCalendar.set(Calendar.MILLISECOND, newCalendar.get(Calendar.MILLISECOND));
        set(currentCalendar.getTime());
    }

    public Date getDate() {
        return new Date(tglMilis);
    }
    public long getMilis() {
        return tglMilis;
    }

    public String getTglString() {
        return DateUtils.dateToString(FORMAT_DATETIME_FULL, getDate());
    }
    public String getTglString(String dateFormat) {
        return DateUtils.dateToString(dateFormat, getDate());
    }
    public String getTglString(String dateFormat, Locale locale) {
        return DateUtils.dateToString(dateFormat, getDate(), locale);
    }
}
