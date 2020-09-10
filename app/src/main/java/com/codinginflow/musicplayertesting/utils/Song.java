package com.codinginflow.musicplayertesting.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class Song  {
    private String trackName;
    private String artistName;
    private String trackPath;
    private boolean isPlaying;


    public Song(String trackName, String artistName, String trackPath) {
        this.trackName = trackName;
        this.artistName = artistName;
        this.trackPath = trackPath;
        //this.albumArtPath = albumArtPath;
        isPlaying = false;
    }


    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getTrackPath() {
        return trackPath;
    }

    public void setTrackPath(String trackPath) {
        this.trackPath = trackPath;
    }

//    public String getAlbumArtPath() {
//        return albumArtPath;
//    }
//
//    public void setAlbumArtPath(String albumArtPath) {
//        this.albumArtPath = albumArtPath;
//    }
}
