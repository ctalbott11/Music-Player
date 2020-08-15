package com.aci.android.musicplayer;

/**
 * Created by C on 7/16/2017.
 */

public class Song {
    public String title;
    public String artist;
    public String album;
    public String length;
    public String data;

    public Song(String title, String artist, String album, String length, String data) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.length = length;
        this.data = data;
    }
    public Song(String title, String duration){
        this.title = title;
        this.length = duration;
    }
}
