package com.aci.android.musicplayer.artist;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ArtistRepository {
    public static ArtistRepository instance;
    ArrayList<String> artists = new ArrayList<>();

    @NonNull
    private static MutableLiveData<ArrayList<String>> artistList = new MutableLiveData<>();

    public static ArtistRepository getInstance(){
        if(instance == null){
            synchronized (ArtistRepository.class){
                if(instance == null){
                    instance = new ArtistRepository();
                }
            }
        }
        return instance;
    }

    @NonNull
    public MutableLiveData<ArrayList<String>> getArtists(){
        return artistList;
    }

    public void fetchArtists(Context context){
        new Thread(() -> {
            try {
                String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
                String[] projection = {
                        MediaStore.Audio.Media.ARTIST
                };

                Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        projection, selection, null, null);
                assert cursor != null;
                try {
                    cursor.moveToFirst();
                    do {
                        artists.add(cursor.getString(0));
                    } while (cursor.moveToNext());
                } catch(CursorIndexOutOfBoundsException e){
                    e.printStackTrace();
                }
                cursor.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            Set<String> set = new HashSet<>(artists);
            artists.clear();
            artists.addAll(set);
            Collections.sort(artists);
        }).start();


        artistList.postValue(artists);
    }
}
