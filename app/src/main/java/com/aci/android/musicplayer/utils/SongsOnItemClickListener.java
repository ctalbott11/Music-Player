package com.aci.android.musicplayer.utils;

import android.view.View;

import com.aci.android.musicplayer.paging.SongsViewModel;

import java.io.IOException;

/**
 * Created by C on 7/14/2017.
 */

public interface SongsOnItemClickListener {

    void onItemClick(int position);
    void onPlayClick(View v);
    void onPreviousSongClick();
    void onNextSongClick();
}
