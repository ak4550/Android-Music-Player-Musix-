package com.codinginflow.musicplayertesting.utils;

import android.media.MediaPlayer;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.IOException;

public class MyPlayer extends MediaPlayer  {
    private static MyPlayer instance ;



    public static MyPlayer getPlayerInstance(){
        if(instance == null){
            instance = new MyPlayer();
            return instance;
        }else{
            return null;
        }
    }

    public static void clearInstance(){
        instance.release();
        instance = null;
    }

    private MyPlayer(){

    }



}
