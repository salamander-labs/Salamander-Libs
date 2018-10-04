package com.salamander.salamander_network;

import android.os.Parcel;
import android.os.Parcelable;

public class RetroStatus implements Parcelable {

    private boolean isSuccess;
    private int StatusCode;
    private String Header;
    private String URL;
    private String Title, Message, Query;

    public RetroStatus() {}

    public RetroStatus(boolean isSuccess, String title, String message, String query) {
        this.isSuccess = isSuccess;
        this.Title = title;
        this.Message = message;
        this.Query = query;
    }

    public RetroStatus(boolean isSuccess, String message, String query) {
        this.isSuccess = isSuccess;
        this.Message = message;
        this.Query = query;
    }

    public RetroStatus(boolean isSuccess, String message) {
        this(isSuccess, message, null);
    }

    protected RetroStatus(Parcel in) {
        isSuccess = in.readByte() != 0;
        StatusCode = in.readInt();
        Header = in.readString();
        URL = in.readString();
        Title = in.readString();
        Message = in.readString();
        Query = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isSuccess ? 1 : 0));
        dest.writeInt(StatusCode);
        dest.writeString(Header);
        dest.writeString(URL);
        dest.writeString(Title);
        dest.writeString(Message);
        dest.writeString(Query);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RetroStatus> CREATOR = new Creator<RetroStatus>() {
        @Override
        public RetroStatus createFromParcel(Parcel in) {
            return new RetroStatus(in);
        }

        @Override
        public RetroStatus[] newArray(int size) {
            return new RetroStatus[size];
        }
    };

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getQuery() {
        return Query;
    }

    public void setQuery(String query) {
        this.Query = query.replace("''", "'");
    }

    public String getHeader() {
        return Header;
    }

    public void setHeader(String header) {
        Header = header;
    }

    public int getStatusCode() {
        return StatusCode;
    }

    public void setStatusCode(int statusCode) {
        StatusCode = statusCode;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
