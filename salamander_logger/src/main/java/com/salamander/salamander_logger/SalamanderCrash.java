package com.salamander.salamander_logger;

import android.content.Context;

public class SalamanderCrash {

    private static volatile SalamanderCrash sSalamanderCrash;

    //private constructor.
    private SalamanderCrash(){

        //Prevent form the reflection api.
        if (sSalamanderCrash != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static SalamanderCrash getInstance(Context context) {
        //Double check locking pattern
        if (sSalamanderCrash == null) { //Check for the first time

            synchronized (SalamanderCrash.class) {   //Check for the second time.
                //if there is no instance available... create new one
                if (sSalamanderCrash == null) sSalamanderCrash = new SalamanderCrash();
            }
        }

        return sSalamanderCrash;
    }
}
