package com.aci.android.musicplayer.paging;

import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import com.aci.android.musicplayer.Song;
import com.aci.android.musicplayer.utils.AppApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SongsDataSource extends PositionalDataSource<Song> {

    //the size of a page that we want
    public static final int PAGE_SIZE = 20;

    //we will start from the first page which is 1
    //private static final int FIRST_PAGE = 1;


    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<Song> callback) {

        Log.i("test", Integer.toString(getSongs(params.requestedLoadSize, params.requestedStartPosition).size()));

        callback.onResult(getSongs(params.requestedLoadSize, params.requestedStartPosition), 0);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Song> callback) {
        callback.onResult(getSongs(params.loadSize, params.startPosition));
    }

    private List<Song> getSongs(int limit, int offset){
        List<Song> list = new ArrayList<>();

        try {
            String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
            String[] projection = {
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.ALBUM
            };

            Cursor cursor = AppApplication.getInstance().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection, selection, null, MediaStore.Audio.Media.DISPLAY_NAME + " ASC LIMIT " + limit + " OFFSET " + offset);
            assert cursor != null;
            while (cursor.moveToNext()) {
                Song song = new Song(cursor.getString(2), cursor.getString(1), cursor.getString(6),
                        cursor.getString(5), cursor.getString(3));
                list.add(song);
            }
            Set<Song> set = new HashSet<>(list);
            list.clear();
            list.addAll(set);

            cursor.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        Collections.sort(list, (lhs, rhs) -> lhs.title.compareTo(rhs.title));

        return list;
    }

}
