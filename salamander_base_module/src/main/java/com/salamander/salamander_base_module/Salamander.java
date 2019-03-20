package com.salamander.salamander_base_module;

import android.content.Context;

import java.io.Serializable;

public class Salamander implements Serializable {

    private static volatile Salamander sSoleInstance;
    private Context appContext;

    //private constructor.
    private Salamander(){

        //Prevent form the reflection api.
        if (sSoleInstance != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static Salamander getInstance() {
        if (sSoleInstance == null) { //if there is no instance available... create new one
            synchronized (Salamander.class) {
                if (sSoleInstance == null) sSoleInstance = new Salamander();
            }
        }

        return sSoleInstance;
    }

    //Make singleton from serialize and deserialize operation.
    protected Salamander readResolve() {
        return getInstance();
    }

    public void setContext(Context context) {
        this.appContext = context;
    }

    public Context getContext() {
        return this.appContext;
    }

    public String getTAG() {
        if (appContext != null) {
            String[] splitString = appContext.getPackageName().split("\\.");
            return splitString[splitString.length - 1];
        } else {
            String[] splitString = BuildConfig.APPLICATION_ID.split("\\.");
            return splitString[splitString.length - 1];
        }
    }
}