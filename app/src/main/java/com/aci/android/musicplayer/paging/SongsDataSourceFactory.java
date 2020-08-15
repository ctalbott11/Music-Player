package com.aci.android.musicplayer.paging;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.aci.android.musicplayer.Song;

import org.jetbrains.annotations.NotNull;


public class SongsDataSourceFactory extends DataSource.Factory<Integer, Song> {

    private static MutableLiveData<SongsDataSource> mutableLiveData;

    public SongsDataSourceFactory(){
        mutableLiveData = new MutableLiveData<>();
    }
    @NotNull
    @Override
    public DataSource<Integer, Song> create() {
        SongsDataSource songsDataSource = new SongsDataSource();
        mutableLiveData.postValue(songsDataSource);
        return new SongsDataSource();
    }

    public static MutableLiveData<SongsDataSource> getMutableLiveData() {
        return mutableLiveData;
    }
}
