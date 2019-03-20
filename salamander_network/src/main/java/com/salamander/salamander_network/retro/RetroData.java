package com.salamander.salamander_network.retro;

import android.os.Parcel;
import android.os.Parcelable;

import com.salamander.salamander_network.JSON;

import org.json.JSONObject;

import java.util.ArrayList;

public class RetroData implements Parcelable {

    private int Code;
    private String ActivityName, ClassName, MethodName, Parameter;
    private String Result, URL, Header, Status;
    private RetroStatus retroStatus = new RetroStatus();
    private ArrayList<com.salamander.salamander_network.retro.Parameter> Parameters = new ArrayList<>();

    public RetroData() {
    }

    protected RetroData(Parcel in) {
        Code = in.readInt();
        ActivityName = in.readString();
        ClassName = in.readString();
        MethodName = in.readString();
        Parameter = in.readString();
        Result = in.readString();
        URL = in.readString();
        Header = in.readString();
        Status = in.readString();
        retroStatus = in.readParcelable(RetroStatus.class.getClassLoader());
        Parameters = in.createTypedArrayList(com.salamander.salamander_network.retro.Parameter.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Code);
        dest.writeString(ActivityName);
        dest.writeString(ClassName);
        dest.writeString(MethodName);
        dest.writeString(Parameter);
        dest.writeString(Result);
        dest.writeString(URL);
        dest.writeString(Header);
        dest.writeString(Status);
        dest.writeParcelable(retroStatus, flags);
        dest.writeTypedList(Parameters);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RetroData> CREATOR = new Creator<RetroData>() {
        @Override
        public RetroData createFromParcel(Parcel in) {
            return new RetroData(in);
        }

        @Override
        public RetroData[] newArray(int size) {
            return new RetroData[size];
        }
    };

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

    public String getParameter() {
        return Parameter;
    }

    public void setParameter(String parameter) {
        Parameter = parameter;
    }

    public RetroStatus getRetroStatus() {
        return retroStatus;
    }

    public void setRetroStatus(RetroStatus retroStatus) {
        this.retroStatus = retroStatus;
    }

    public boolean isSuccess() {
        return retroStatus != null && retroStatus.isSuccess();
    }

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }

    public String getErrorMsg() {
        return retroStatus.getMessage();
    }

    public JSONObject getJSONData() {
        return JSON.getJSONObject(JSON.toJSONObject(getResult()), "data");
    }

    public String getData() {
        return JSON.getStringOrNull(JSON.toJSONObject(getResult()), "data");
    }

    public String getActivityName() {
        return ActivityName;
    }

    public void setActivityName(String activityName) {
        ActivityName = activityName;
    }

    public ArrayList<Parameter> getParameters() {
        return Parameters;
    }

    public void setParameters(ArrayList<Parameter> parameters) {
        Parameters = parameters;
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int statusCode) {
        Code = statusCode;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getHeader() {
        return Header;
    }

    public void setHeader(String header) {
        Header = header;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
