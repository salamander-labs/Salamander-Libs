package com.salamander.network.retro;

import android.os.Parcel;
import android.os.Parcelable;

public class Parameter implements Parcelable {

    private String Name, Value;

    public Parameter(String name, String value) {
        this.Name = name;
        this.Value = value;
    }

    protected Parameter(Parcel in) {
        Name = in.readString();
        Value = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Name);
        dest.writeString(Value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Parameter> CREATOR = new Creator<Parameter>() {
        @Override
        public Parameter createFromParcel(Parcel in) {
            return new Parameter(in);
        }

        @Override
        public Parameter[] newArray(int size) {
            return new Parameter[size];
        }
    };

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }
}
