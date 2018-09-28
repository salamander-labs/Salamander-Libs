package com.salamander.salamander_network.log;

import android.os.Parcel;
import android.os.Parcelable;

import com.salamander.salamander_network.RetroData;

public class NetworkLog implements Parcelable {

    private int UserID = 0;
    private RetroData retroData = new RetroData();

    public NetworkLog() {}
    public NetworkLog(RetroData retroData) {
        this.retroData = retroData;
    }
    public NetworkLog(int userID, RetroData retroData) {
        this.UserID = userID;
        this.retroData = retroData;
    }

    protected NetworkLog(Parcel in) {
        UserID = in.readInt();
        retroData = in.readParcelable(RetroData.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(UserID);
        dest.writeParcelable(retroData, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NetworkLog> CREATOR = new Creator<NetworkLog>() {
        @Override
        public NetworkLog createFromParcel(Parcel in) {
            return new NetworkLog(in);
        }

        @Override
        public NetworkLog[] newArray(int size) {
            return new NetworkLog[size];
        }
    };

    public RetroData getRetroData() {
        return retroData;
    }

    public void setRetroData(RetroData retroData) {
        this.retroData = retroData;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }
}
