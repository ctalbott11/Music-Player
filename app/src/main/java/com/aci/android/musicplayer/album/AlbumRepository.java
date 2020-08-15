package com.aci.android.musicplayer.album;

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

public class AlbumRepository {
    public static AlbumRepository instance;

    ArrayList<String> albums = new ArrayList<>();

    @NonNull
    private static MutableLiveData<ArrayList<String>> albumList = new MutableLiveData<>();

    public static AlbumRepository getInstance(){
        if(instance == null){
            synchronized (AlbumRepository.class){
                if(instance == null){
                    instance = new AlbumRepository();
                }
            }
        }
        return instance;
    }

    @NonNull
    public MutableLiveData<ArrayList<String>> getAlbums(){
        return albumList;
    }

    public void fetchAlbums(Context context){
        new Thread(() -> {
            try {
                String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
                String[] projection = {
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.YEAR
                };

                Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        projection, selection, null, null);
                assert cursor != null;
                try {
                    cursor.moveToFirst();
                    do {
                        albums.add(cursor.getString(0));
                    } while (cursor.moveToNext());
                } catch(CursorIndexOutOfBoundsException e){
                    e.printStackTrace();
                }
                cursor.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            Set<String> set = new HashSet<>(albums);
            albums.clear();
            albums.addAll(set);
            Collections.sort(albums);

            albumList.postValue(albums);

        }).start();

    }
}
