package com.salamander.salamander_logger;

import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import com.salamander.salamander_base_module.Utils;
import com.salamander.salamander_base_module.object.Tanggal;

import org.json.JSONObject;

public class ErrorLog implements Parcelable {

    public static final String ERROR_ID = "ID";
    public static final String ERROR_DEVICE_MODEL = "Model";
    public static final String ERROR_ANDROID_VERSION_SDK = "SDK";
    public static final String ERROR_ANDROID_VERSION_RELEASE = "Release";
    public static final String ERROR_ANDROID_FINGERPRINT = "Fingerprint";
    public static final String ERROR_PACKAGE_NAME = "Package";
    public static final String ERROR_CLASS_NAME = "Class";
    public static final String ERROR_METHOD_NAME = "Method";
    public static final String ERROR_LINE_NUMBER = "LineNumber";
    public static final String ERROR_EXCEPTION = "Exception";
    public static final String ERROR_MESSAGE = "Message";
    public static final String ERROR_LOGCAT = "LogCat";
    public static final String ERROR_TANGGAL = "Tanggal";

    private int ID = 0, LineNumber = 0;
    private String ClassName, MethodName, Message, LogCat, LogCatFile, Exception;
    private Tanggal ErrorDate;

    public ErrorLog() {}

    protected ErrorLog(Parcel in) {
        ID = in.readInt();
        LineNumber = in.readInt();
        ClassName = in.readString();
        MethodName = in.readString();
        Message = in.readString();
        LogCatFile = in.readString();
        Exception = in.readString();
        ErrorDate = in.readParcelable(Tanggal.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeInt(LineNumber);
        dest.writeString(ClassName);
        dest.writeString(MethodName);
        dest.writeString(Message);
        dest.writeString(LogCatFile);
        dest.writeString(Exception);
        dest.writeParcelable(ErrorDate, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ErrorLog> CREATOR = new Creator<ErrorLog>() {
        @Override
        public ErrorLog createFromParcel(Parcel in) {
            return new ErrorLog(in);
        }

        @Override
        public ErrorLog[] newArray(int size) {
            return new ErrorLog[size];
        }
    };

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getLineNumber() {
        return LineNumber;
    }

    public void setLineNumber(int lineNumber) {
        LineNumber = lineNumber;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public String getMethodName() {
        return MethodName;
    }

    public void setMethodName(String methodName) {
        MethodName = methodName;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public Tanggal getErrorDate() {
        return ErrorDate;
    }

    public void setErrorDate(Tanggal errorDate) {
        ErrorDate = errorDate;
    }

    public String getLogCatFile() {
        return LogCatFile;
    }

    public void setLogCatFile(String logCatFile) {
        LogCatFile = logCatFile;
    }

    public String getException() {
        return Exception;
    }

    public void setException(String exception) {
        Exception = exception;
    }

    public JSONObject getAsJSON(Context context) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ERROR_DEVICE_MODEL, Build.MODEL);
            jsonObject.put(ERROR_ANDROID_VERSION_SDK, Build.VERSION.SDK_INT);
            jsonObject.put(ERROR_ANDROID_VERSION_RELEASE, Build.VERSION.RELEASE);
            jsonObject.put(ERROR_ANDROID_FINGERPRINT, Build.FINGERPRINT);
            jsonObject.put(ERROR_CLASS_NAME, ClassName);
            jsonObject.put(ERROR_METHOD_NAME, MethodName);
            jsonObject.put(ERROR_LINE_NUMBER, LineNumber);
            jsonObject.put(ERROR_EXCEPTION, Exception);
            jsonObject.put(ERROR_MESSAGE, Message);
            jsonObject.put(ERROR_LOGCAT, LogCat);
            jsonObject.put(ERROR_TANGGAL, ErrorDate.getTglString());
            jsonObject.put(ErrorLog.ERROR_PACKAGE_NAME, context.getApplicationContext().getPackageName());
        } catch (Exception e) {
            Utils.showLog(e);
        }
        return jsonObject;
    }

    public String getLogCat() {
        return LogCat;
    }

    public void setLogCat(String logCat) {
        LogCat = logCat;
    }
}
