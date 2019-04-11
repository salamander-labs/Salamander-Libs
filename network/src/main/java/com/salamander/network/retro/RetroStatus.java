package com.salamander.network.retro;

import android.os.Parcel;
import android.os.Parcelable;

import com.salamander.core.Utils;

import org.json.JSONObject;

public class RetroStatus implements Parcelable {

    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_ERROR = 2;
    public static final int STATUS_WARNING = 3;

    private boolean isSuccess;
    private int Code;
    private String Title, Status, Message, Query;

    public RetroStatus() {
    }

    protected RetroStatus(Parcel in) {
        isSuccess = in.readByte() != 0;
        Code = in.readInt();
        Title = in.readString();
        Status = in.readString();
        Message = in.readString();
        Query = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isSuccess ? 1 : 0));
        dest.writeInt(Code);
        dest.writeString(Title);
        dest.writeString(Status);
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

    private void set(String jsonObject) {
        try {
            JSONObject jsonObjectStatus = new JSONObject(jsonObject);

            if (jsonObjectStatus.has(RetroConst.RETRO_STATUS)) {
                String status = jsonObjectStatus.getString(RetroConst.RETRO_STATUS);
                try {
                    int statusCode = Integer.valueOf(status);
                    setCode(statusCode);
                } catch (Exception e) {
                    setStatus(status);
                }
            }

            if (jsonObjectStatus.has(RetroConst.RETRO_STATUS_CODE)) {
                setCode(jsonObjectStatus.getInt(RetroConst.RETRO_STATUS_CODE));
            }

            if (jsonObjectStatus.has(RetroConst.RETRO_TITLE))
                setTitle(jsonObjectStatus.getString(RetroConst.RETRO_TITLE).trim());
            if (jsonObjectStatus.has(RetroConst.RETRO_MSG))
                setMessage(jsonObjectStatus.getString(RetroConst.RETRO_MSG).trim());
            else if (jsonObjectStatus.has(RetroConst.RETRO_MESSAGE))
                setMessage(jsonObjectStatus.getString(RetroConst.RETRO_MESSAGE).trim());
            else if (jsonObjectStatus.has(RetroConst.RETRO_MESSAGE))
                setMessage(jsonObjectStatus.getString(RetroConst.RETRO_MESSAGE).trim());
            if (jsonObjectStatus.has(RetroConst.RETRO_SQL))
                setQuery(jsonObjectStatus.getString(RetroConst.RETRO_SQL).trim());
        } catch (Exception e) {
            Utils.showLog(e);
        }
    }

    public RetroStatus(String jsonString) {
        set(jsonString);
    }

    public RetroStatus(JSONObject jsonObject) {
        set(jsonObject.toString());
    }

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

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
        setSuccess(code == STATUS_SUCCESS);
        if (Utils.isEmpty(Status)) {
            switch (code) {
                case STATUS_SUCCESS:
                    Status = RetroConst.STATUS_SUCCESS;
                    break;
                case STATUS_WARNING:
                    Status = RetroConst.STATUS_WARNING;
                    break;
                default:
                    Status = RetroConst.STATUS_ERROR;
                    break;
            }
        }
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
        setSuccess(status.equals(RetroConst.STATUS_SUCCESS));
        if (Code == 0 && !Utils.isEmpty(Status)) {
            if (status.equals(RetroConst.STATUS_SUCCESS))
                Code = STATUS_SUCCESS;
            else if (status.equals(RetroConst.STATUS_ERROR))
                Code = STATUS_ERROR;
            else if (status.equals(RetroConst.STATUS_WARNING))
                Code = STATUS_WARNING;
            else Code = 0;
        }
    }
}
