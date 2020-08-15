package com.aci.android.musicplayer.album.albumsongs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aci.android.musicplayer.R;
import com.aci.android.musicplayer.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AlbumSongsAdapter extends RecyclerView.Adapter<AlbumSongsAdapter.CustomViewHolder> {
    private final ArrayList<Song> albumSongsList;
    private final OnAlbumSongsClick onAlbumSongsClick;

    AlbumSongsAdapter(Context context, ArrayList<Song> albumSongs){
        this.albumSongsList = albumSongs;
        this.onAlbumSongsClick = (OnAlbumSongsClick) context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.song_row, viewGroup, false);
        return new CustomViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder customViewHolder, int i) {
        Song song = albumSongsList.get(i);
        customViewHolder.tvArtist.setText(song.artist);
        customViewHolder.tvTitle.setText(song.title);
        customViewHolder.tvAlbum.setText(song.album);

        int p = Integer.parseInt(song.length);
        int result = (p / 1000) % 60;

        String s = String.format(Locale.getDefault(), "%2d:%02d", TimeUnit.MILLISECONDS.toMinutes(p), result);
        customViewHolder.tvTrackLength.setText(s);


    }

    @Override
    public int getItemCount() {
        return (albumSongsList == null) ? 0 : albumSongsList.size();
    }
    public interface OnAlbumSongsClick{
        void onAlbumSongsClick(int pos) throws IOException;

    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final TextView tvArtist;
        final TextView tvTitle;
        final TextView tvAlbum;
        final TextView tvTrackLength;


        CustomViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            tvArtist = view.findViewById(R.id.list_artist);
            tvTitle = view.findViewById(R.id.list_title);
            tvAlbum = view.findViewById(R.id.list_album);
            tvTrackLength = view.findViewById(R.id.row_track_length);
        }
        @Override
        public void onClick(View view) {
            try {
                onAlbumSongsClick.onAlbumSongsClick(getAdapterPosition());
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
